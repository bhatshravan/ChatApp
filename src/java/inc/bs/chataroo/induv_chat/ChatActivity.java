package inc.bs.chataroo.induv_chat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.misc.GetTimeAgo;
import inc.bs.chataroo.misc.Loger;
import inc.bs.chataroo.models.Messages;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;
    private Toolbar mChatToolbar;

    boolean notfriendme=false;
    private Menu menu;

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

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 200;
    private int mCurrentPage = 1;

    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;

    String image;
    private String user1,user2,userName,myuserName,user1name,user2name,token_me,token,token1,token2;

    //New Solution
    private int itemPos = 0;

    private String mLastKey = "";
    private String mPrevKey = "";
    Boolean blocked=false,first=false;
    TextView typingbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.induv_chat_chat);

        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setTitle("");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra(Constants.FIREBASE_USERS_UID);
        userName = getIntent().getStringExtra(Constants.FIREBASE_USERNAME);
        myuserName = getIntent().getStringExtra(Constants.FIREBASE_MYUSERNAME);
        token = getIntent().getStringExtra(Constants.FIREBASE_FCM_TOKEN);
        if(token == null || token.equals("nope"))
        {
            mRootRef.child("users").child(mChatUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    token=dataSnapshot.child(Constants.FIREBASE_FCM_TOKEN).getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        token_me = getIntent().getStringExtra(Constants.FIREBASE_MY_FCM_TOKEN);

        if(mChatUser.equals(mCurrentUserId))
        {
            Toast.makeText(getApplicationContext(),"As much as we appreciate it,try chatting with others and not yourself",Toast.LENGTH_LONG).show();
            finish();
        }

        if(mCurrentUserId.compareTo(mChatUser)<1) {
            user1=mCurrentUserId;
            user2=mChatUser;

            user1name=myuserName;
            user2name=userName;

            token1=token_me;
            token2=token;
        }
        else {
            user1=mChatUser;
            user2=mCurrentUserId;


            user1name=userName;
            user2name=myuserName;

            token1=token;
            token2=token_me;
        }

        chatRef = FirebaseDatabase.getInstance().getReference().child("chat").child("private").child(user1).child(user2);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.induv_chat_chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        // ---- Custom Action bar Items ----

        mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);

        mChatAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);
        typingbtn = findViewById(R.id.typingbtn);

        mAdapter = new MessageAdapter(ChatActivity.this,messagesList,user1,user2);

        //mAdapter.setHasStableIds(true);

        mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);

        //------- IMAGE STORAGE ---------
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mTitleView.setText(userName);

        //Check if first chat
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild("first")) {
                    first=true;
                    try {
                        updateMenuTitles();
                    }
                    catch (Exception e){

                    }
                    notfriendme=true;
                }
                else
                {
                    String friend = dataSnapshot.child("banned").getValue().toString();
                    if(friend.equals(mCurrentUserId))
                    {
                        Toast.makeText(ChatActivity.this,user2name+" " +getString(R.string.asked),Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ChatActivity.this);
                        builder1.setMessage(user2name+" "+getString(R.string.asked));
                        builder1.setCancelable(false);
                        builder1.setNegativeButton(
                                "Block user",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mRootRef.child("chatlists").child("private").child(mCurrentUserId).child(mChatUser).child("friend").setValue("banned");
                                        chatRef.child("banned").setValue("banned"+mCurrentUserId);
                                        Toast.makeText(getApplicationContext(),"User is blocked",Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                        builder1.setPositiveButton(
                                "Accept chat request",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mRootRef.child("chatlists").child("private").child(mCurrentUserId).child(mChatUser).child("friend").setValue("friend");
                                        chatRef.child("banned").setValue("friend");
                                        updateMenuTitles();
                                        dialog.cancel();
                                    }
                                }
                        );
                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                    else if(friend.equals("banned"+mChatUser))
                    {
                        Toast.makeText(getApplicationContext(),getString(R.string.induv_banned1)+user2name,Toast.LENGTH_SHORT).show();
                        blocked=true;
                        notfriendme=true;
                        //finish();
                    }
                    else if(friend.equals("banned"+mCurrentUserId))
                    {
                        blocked=true;
                        Toast.makeText(getApplicationContext(),"User is blocked",Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ChatActivity.this);
                        builder1.setMessage(R.string.induv_unblock);
                        builder1.setCancelable(false);
                        builder1.setNegativeButton(
                                R.string.induv_cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        blocked=true;
                                        dialog.cancel();

                                    }
                                });
                        builder1.setPositiveButton(
                                R.string.induv_accept,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mRootRef.child("chatlists").child("private").child(mCurrentUserId).child(mChatUser).child("friend").setValue("friend");
                                        chatRef.child("banned").setValue("friend");
                                        blocked=false;
                                        updateMenuTitles();
                                        dialog.cancel();
                                    }
                                }
                        );
                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                    else if(friend.equals(mChatUser))
                    {
                        first=true;
                        updateMenuTitles();
                        notfriendme=true;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        bannedListen();

        loadMessages();


        //Check if chater is online
        mRootRef.child("users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                image = dataSnapshot.child(Constants.FIREBASE_USERS_THUMB).getValue().toString();
                try {
                    Picasso.with(ChatActivity.this).load(image).error(R.drawable.default_avatar).placeholder(R.drawable.default_avatar).into(mProfileImage);
                }
                catch (Exception e)
                {
                    Glide.with(ChatActivity.this).load(R.drawable.default_avatar).into(mProfileImage);
                }
                GetTimeAgo getTimeAgo = new GetTimeAgo();

                long lastTime = Long.parseLong(online);

                String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());

                mLastSeenView.setText(lastSeenTime);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mChatMessageView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                typingbtn.setVisibility(View.GONE);
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                typingbtn.setVisibility(View.GONE);
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                typingbtn.setVisibility(View.VISIBLE);
            }
        });

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

                if(!blocked) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
                }
                else
                    Toast.makeText(getApplicationContext(),R.string.induv_nosendmessage,Toast.LENGTH_SHORT).show();

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

        mRootRef.child("users").child(mCurrentUserId).child("online").setValue(ServerValue.TIMESTAMP);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRootRef.child("users").child(mCurrentUserId).child("online").setValue(ServerValue.TIMESTAMP);
            }
        }, 15000);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();
            final String user_ref = "chat/private/" + user1 + "/" + user2;

            DatabaseReference user_message_push =chatRef.child("messages").push();

            final String push_id = user_message_push.getKey();

            StorageReference filepath = mImageStorage.child("images").child(mCurrentUserId).child("private_message_images").child( push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){

                        String download_url = task.getResult().getDownloadUrl().toString();


                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", mCurrentUserId);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put( "messages/"+push_id, messageMap);

                        mRootRef.child("chatlists").child("private").child(mCurrentUserId).child(mChatUser).child("last").setValue("Image.");
                        mRootRef.child("chatlists").child("private").child(mChatUser).child(mCurrentUserId).child("last").setValue("Image.");

                        mRootRef.child("chatlists").child("private").child(mCurrentUserId).child(mChatUser).child("last_time").setValue(ServerValue.TIMESTAMP);
                        mRootRef.child("chatlists").child("private").child(mChatUser).child(mCurrentUserId).child("last_time").setValue(ServerValue.TIMESTAMP);

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

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPos++, message);
                }
                else
                {
                    mPrevKey = mLastKey;
                }



                // Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);

                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);

                //   mLinearLayout.scrollToPositionWithOffset(10, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                //       mAdapter.notifyDataSetChanged();

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

    private void loadMessages() {

        DatabaseReference messageRef = chatRef.child("messages");
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
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
            public void onChildRemoved(DataSnapshot dataSnapshot) {            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {            }

            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });
    }

    private void sendMessage() {

        if(first)
            firstChat();

        String message = mChatMessageView.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            final String user_ref = "chat/private/" + user1 + "/" + user2;

            DatabaseReference user_message_push = chatRef.child("messages").push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put("messages/" + push_id, messageMap);

            mChatMessageView.setText("");


            chatRef.child(mChatUser).child("seen").setValue(false);
            chatRef.child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

            chatRef.child(mCurrentUserId).child("seen").setValue(true);
            chatRef.child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            chatRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null)
                    {

                        //Log.d("CHAT_LOG", databaseError.getMessage());
                        Toast.makeText(getApplicationContext(), R.string.induv_eroor, Toast.LENGTH_SHORT).show();

                    }
                }
            });
            mRootRef.child("chatlists").child("private").child(mCurrentUserId).child(mChatUser).child("last").setValue(message);
            mRootRef.child("chatlists").child("private").child(mChatUser).child(mCurrentUserId).child("last").setValue(message);

            mRootRef.child("chatlists").child("private").child(mCurrentUserId).child(mChatUser).child("last_time").setValue(ServerValue.TIMESTAMP);
            mRootRef.child("chatlists").child("private").child(mChatUser).child(mCurrentUserId).child("last_time").setValue(ServerValue.TIMESTAMP);

        }
    }

    Loger ll=new Loger();

    void firstChat()
    {

        notfriendme=true;
        chatRef.child(mChatUser).child("seen").setValue(false);
        chatRef.child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);


        Map chatAddMap1 = new HashMap();
        chatAddMap1.put("seen", false);
        chatAddMap1.put("timestamp", ServerValue.TIMESTAMP);

        //Set messages as seen value
        chatRef.child(mCurrentUserId).child("seen").setValue(true);


        Map chatAddMap = new HashMap();
        chatAddMap.put("first", false);
        chatAddMap.put("banned", mChatUser);
        chatAddMap.put("user1", user1name);
        chatAddMap.put("user2", user2name);
        chatAddMap.put("token1", token1);
        chatAddMap.put("token2", token2);
        chatAddMap.put("by", myuserName);
        chatAddMap.put("created", ServerValue.TIMESTAMP);

        Map chatUserMap = new HashMap();
        chatUserMap.put("chat/private/" + user1 + "/" + user2, chatAddMap);


        mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError != null){

                }

            }
        });
        Map chatUserMap2 = new HashMap();
        chatUserMap.put("chat/private/" + user1 + "/" + user2+"/"+user2, chatAddMap1);


        mRootRef.updateChildren(chatUserMap2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError != null){

                }

            }
        });

        Map m1=new HashMap();
        m1.put("friend","friend");
        m1.put("uid",mChatUser);
        m1.put("username",userName);
        m1.put("token",token);
        m1.put("by",myuserName);


        mRootRef.child("chatlists").child("private").child(mCurrentUserId).child(mChatUser).setValue(m1);

        Map m2=new HashMap();
        m2.put("friend","nofriend");
        m2.put("uid",mCurrentUserId);
        m2.put("username",myuserName);
        m2.put("token",token_me);
        m2.put("by",myuserName);
        mRootRef.child("chatlists").child("private").child(mChatUser).child(mCurrentUserId).setValue(m2);


        //mRootRef.child("chatlists").child("private").child(mChatUser).child(mCurrentUserId).child("friend").setValue("nofriend");

        bannedListen();
        first=false;
    }
    void bannedListen()
    {
        chatRef.child("banned").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.hasChild("banned")) {
                    String friend = dataSnapshot.child("banned").getValue().toString();
                    if (friend.equals("banned" + mChatUser) || friend.equals("banned" + mChatUser)) {
                        blocked = true;
                        notfriendme=true;
                    }
                    else if(friend.equals(mChatUser))
                        notfriendme=true;
                    else
                        notfriendme=false;
                    updateMenuTitles();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.chat_priv_menu, menu);
            this.menu=menu;
            return true;
    }

    private void updateMenuTitles() {
        MenuItem bedMenuItem = menu.findItem(R.id.chat_block);
        if(notfriendme)
        {
            bedMenuItem.setTitle("--Disabled--");
        }
        else if (blocked) {
            bedMenuItem.setTitle("Unblock user");
        } else {
            bedMenuItem.setTitle("Block user");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if(item.getItemId() == R.id.chat_clear) {

            mRootRef.child("chatlists").child("private").child(mCurrentUserId).child(mChatUser).child("messages").setValue(null);

            finish();
            return true;
        }

        if(item.getItemId() == R.id.chat_block){
            if(notfriendme)
            {
                    Toast.makeText(getApplicationContext(),"You have been blocked by other user or chat request is not accepted yet",Toast.LENGTH_LONG).show();
            }
            else if(!blocked) {
                blocked = true;
                mRootRef.child("chatlists").child("private").child(mCurrentUserId).child(mChatUser).child("friend").setValue("banned");
                chatRef.child("banned").setValue("banned" + mCurrentUserId);
                Toast.makeText(getApplicationContext(), "User is blocked", Toast.LENGTH_SHORT).show();
                updateMenuTitles();
            }
            else if(blocked) {
                mRootRef.child("chatlists").child("private").child(mCurrentUserId).child(mChatUser).child("friend").setValue("friend");
                chatRef.child("banned").setValue("friend");
                blocked=false;
                Toast.makeText(getApplicationContext(), "User is unblocked", Toast.LENGTH_SHORT).show();
                updateMenuTitles();
            }
            else if(first)
                Toast.makeText(getApplicationContext(), "You can't block a user with whom you have not chatted with", Toast.LENGTH_SHORT).show();


        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Activity.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onPause()
    {
        super.onPause();
    }
    @Override
    public void onResume()
    {
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
