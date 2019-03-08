package inc.bs.chataroo.group_chat.priv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import inc.bs.chataroo.R;
import inc.bs.chataroo.group_chat.pub.GroupMessageAdapter;
import inc.bs.chataroo.induv_chat.ChatActivity;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.models.GroupMessagesPublic;

public class GroupChatPrivate extends AppCompatActivity {

    private Toolbar mChatToolbar;

    private DatabaseReference mRootRef;
    private DatabaseReference chatRef;

    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;

    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;

    private final List<GroupMessagesPublic> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private GroupMessageAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 100;
    private int mCurrentPage = 1;

    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;

    String image;
    private String groupname,userName,myuserName,uthumb,groupthumb="",myfcm;
    SharedPreferences pref;
    //New Solution
    private int itemPos = 0;
    boolean firstchat=false;
    private String mLastKey = "";
    private String mPrevKey = "";
    String gid;
    Boolean blocked=false,firstloop=true,first=false;
    TextView typingbtn;
    RecyclerView rlview;
    CircleImageView cl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_public_chat);

        rlview= findViewById(R.id.messages_list);
        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_barg);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setTitle("");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        // mCurrentUserId = getIntent().getStringExtra(Constants.FIREBASE_USERS_UID);
        groupname = getIntent().getStringExtra(Constants.CHAT_GROUP);
        myuserName = getIntent().getStringExtra(Constants.FIREBASE_MYUSERNAME);
        myfcm = getIntent().getStringExtra(Constants.FIREBASE_MY_FCM_TOKEN);
        uthumb =  getIntent().getStringExtra(Constants.FIREBASE_USERS_THUMB);

        if(myuserName==null)
        {
            FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_USERS).child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myuserName = dataSnapshot.child(Constants.FIREBASE_USERNAME).getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }

        chatRef = FirebaseDatabase.getInstance().getReference().child("chat").child("group").child("public").child(groupname);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.group_public_chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        // ---- Custom Action bar Items ----

        mTitleView = (TextView) findViewById(R.id.custom_bar_titleg);
        // mLastSeenView = (TextView) findViewById(R.id.custom_bar_seeng);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_imageg);
        if(uthumb!=null)
            Picasso.with(GroupChatPrivate.this).load(uthumb).error(R.drawable.default_avatar).placeholder(R.drawable.default_avatar).into(mProfileImage);

        mChatAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);
        typingbtn = findViewById(R.id.typingbtn);

        mAdapter = new GroupMessageAdapter(messagesList,GroupChatPrivate.this,mCurrentUserId);
        mAdapter.setHasStableIds(true);

        mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);

         pref = getSharedPreferences(Constants.prefs, Context.MODE_PRIVATE);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        registerForContextMenu(rlview);

        mMessagesList.setAdapter(mAdapter);

        //------- IMAGE STORAGE ---------
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mTitleView.setText(groupname);

        chatRef.child("gid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                   /* DatabaseReference gid_push = mRootRef.child("groups").child("public").child("ids").push();
                    gid = gid_push.getKey();

                    Map messageMap = new HashMap();
                    messageMap.put("created", ServerValue.TIMESTAMP);
                    messageMap.put("author",myuserName);
                    messageMap.put("gid",gid);
                    messageMap.put("name",groupname);


                    mRootRef.child("groups").child("public").child("ids").child(gid).setValue(messageMap);
                    mRootRef.child("groups").child("public").child("names").child(groupname.toLowerCase()).setValue(messageMap);


                    Map messageMap2 = new HashMap();
                    messageMap2.put("name", groupname);
                    messageMap2.put("author",myuserName);
                    messageMap2.put("image","noimage");
                    messageMap2.put("image_thumbnail","noimage");
                    messageMap2.put("gid",gid);

                    chatRef.setValue(messageMap2);*/
                    Toast.makeText(getApplicationContext(),"Chat does not exist",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    gid=dataSnapshot.getValue().toString();
                }




                //Check if first chat
                chatRef.child(Constants.CHAT_GROUP_MEMBERS).child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.hasChild("first")&&firstloop)
                        {
                            firstchat=true;

                            firstloop=false;
                        }
                        else
                        {
                            String friend = dataSnapshot.child("banned").getValue().toString();
                            if(friend.equals("perm"))
                            {
                                Toast.makeText(GroupChatPrivate.this,R.string.group_pub_banned,Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else if(friend.equals("temp"))
                            {
                                String timebanned = dataSnapshot.child("timebanned").getValue().toString();
                                Long timebannedlong=Long.parseLong(timebanned);
                                Object timeStampLong = ServerValue.TIMESTAMP;
                                Long timetime=Long.parseLong(timeStampLong.toString());

                                if(timebannedlong>=timetime)
                                {
                                    chatRef.child(Constants.CHAT_GROUP_MEMBERS).child(mCurrentUserId).child("banned").setValue("nope");
                                }
                                else
                                {
                                    Toast.makeText(GroupChatPrivate.this,"You have been banned for 24 hours",Toast.LENGTH_SHORT).show();
                                    blocked=true;
                                    finish();
                                }

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                loadMessages();

                mChatSendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!blocked)
                            sendMessage();
                        else
                            Toast.makeText(getApplicationContext(),R.string.induv_nosendmessage,Toast.LENGTH_SHORT).show();
                    }
                });


                mChatAddBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent galleryIntent = new Intent();
                        galleryIntent.setType("image/*");
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

                    }
                });

                mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mCurrentPage++;
                        itemPos = 0;
                        loadMoreMessages();
                    }
                });

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        Picasso.with(GroupChatPrivate.this).load(uthumb).error(R.drawable.default_avatar).placeholder(R.drawable.default_avatar).into(mProfileImage);
    }

    public  void firstChat()
    {
        chatRef.child(Constants.CHAT_GROUP_MEMBERS).child(mCurrentUserId).child("banned").setValue("nope");
        chatRef.child(Constants.CHAT_GROUP_MEMBERS).child(mCurrentUserId).child("joined").setValue(ServerValue.TIMESTAMP);
        mRootRef.child("chatlists").child("groups").child(mCurrentUserId).child(gid).child("joined").setValue("yes");
        mRootRef.child("chatlists").child("groups").child(mCurrentUserId).child(gid).child("time_joined").setValue(ServerValue.TIMESTAMP);
        mRootRef.child("chatlists").child("groups").child(mCurrentUserId).child(gid).child("name").setValue(groupname);
        mRootRef.child("chatlists").child("groups").child(mCurrentUserId).child(gid).child("category").setValue("public");
        firstchat=false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(firstchat)
            firstChat();

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();


            //  final String user_ref = "chat/private/group/public/"+groupname;

            DatabaseReference user_message_push =chatRef.child("messages").push();

            final String push_id = user_message_push.getKey();

            StorageReference filepath = mImageStorage.child("images").child(mCurrentUserId).child("public_group").child(push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){

                        String download_url = task.getResult().getDownloadUrl().toString();


                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", mCurrentUserId);
                        messageMap.put("name", myuserName);
                        messageMap.put("image", uthumb);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put( "messages/"+push_id, messageMap);

                        mChatMessageView.setText("");

                        chatRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if(databaseError != null){

                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                }

                            }
                        });


                    }

                }
            });

        }

    }

    private void loadMoreMessages() {

        DatabaseReference messageRef = chatRef.child("messages");

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(100);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GroupMessagesPublic message = dataSnapshot.getValue(GroupMessagesPublic.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    messagesList.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if(itemPos == 1) {

                    mLastKey = messageKey;

                }


                // Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);

                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);

                // mLinearLayout.scrollToPositionWithOffset(10, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRootRef.child("users").child(mCurrentUserId).child("online").setValue(ServerValue.TIMESTAMP);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRootRef.child("users").child(mCurrentUserId).child("online").setValue(ServerValue.TIMESTAMP);
            }
        }, 15000);

    }

    private void loadMessages() {

        DatabaseReference messageRef = chatRef.child("messages");

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GroupMessagesPublic message = dataSnapshot.getValue(GroupMessagesPublic.class);
                // Log.e("CHAT",message.toString());

                itemPos++;

                if(itemPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size() - 1);

                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // inflate menu
        //menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Chat");
        menu.add(0, v.getId(), 0, "Report");
        menu.add(0, v.getId(), 0, "View Profile");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String frl=pref.getString("frl","no");
        String frname=pref.getString("frname","no");
        if (item.getTitle() == "Chat")
        {
            Intent i = new Intent(GroupChatPrivate.this, ChatActivity.class);
            i.putExtra(Constants.FIREBASE_USERS_UID, frl);
            i.putExtra(Constants.FIREBASE_USERNAME, frname);
            i.putExtra(Constants.FIREBASE_MYUSERNAME, myuserName);
            i.putExtra(Constants.FIREBASE_MY_FCM_TOKEN, myfcm);
            i.putExtra(Constants.FIREBASE_FCM_TOKEN, "nope");

            startActivity(i);

        }
        else {
            return  false;
        }
        return super.onContextItemSelected(item);
        //RecyclerViewContextMenuInfo info = (RecyclerViewContextMenuInfo) item.getMenuInfo();
        //handle menu item here
    }

    private void sendMessage() {

        if(firstchat)
            firstChat();

        String message = mChatMessageView.getText().toString();

        if(!TextUtils.isEmpty(message)){

            final String user_ref = "chat/private/group/public/"+groupname;

            DatabaseReference user_message_push =chatRef.child("messages").push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
           messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);
            messageMap.put("name", myuserName);
            messageMap.put("image", uthumb);

            Map messageUserMap = new HashMap();
            messageUserMap.put("messages/"+ push_id, messageMap);

            mChatMessageView.setText("");

            chatRef.child(Constants.CHAT_GROUP_MEMBERS).child(mCurrentUserId).child("seen").setValue(true);
            chatRef.child(Constants.CHAT_GROUP_MEMBERS).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            chatRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        //Log.d("CHAT_LOG", databaseError.getMessage());
                        Toast.makeText(getApplicationContext(),R.string.induv_eroor,Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }

    }
}
