package inc.bs.chataroo.posts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import inc.bs.chataroo.R;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.models.Posts;

public class PostsActivity extends AppCompatActivity {

    private Toolbar mChatToolbar;

    private DatabaseReference rootRef;

    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;

    private final List<Posts> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private PostsAdapter mAdapter;
    private FloatingActionButton npost;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private static final int GALLERY_PICK = 1;

    private int itemPos = 0;

    String gid;
    Boolean blocked = false, firstloop = true, first = false;
    private String uid,myuserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts_list);

        npost = findViewById(R.id.post_new);


        uid =  getIntent().getStringExtra(Constants.FIREBASE_USERS_UID);
        myuserName = getIntent().getStringExtra(Constants.FIREBASE_MYUSERNAME);

        rootRef = FirebaseDatabase.getInstance().getReference();

        mAdapter = new PostsAdapter(messagesList, PostsActivity.this);
        mAdapter.setHasStableIds(true);


        mMessagesList = (RecyclerView) findViewById(R.id.post_list);
        mLinearLayout = new LinearLayoutManager(this);

     //   mMessagesList.s(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        mLinearLayout.setReverseLayout(true);
        mLinearLayout.setStackFromEnd(true);


        mMessagesList.setAdapter(mAdapter);

        npost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PostsActivity.this, NewPostActivity1.class);
                i.putExtra(Constants.FIREBASE_USERS_UID,uid);
                i.putExtra(Constants.FIREBASE_MYUSERNAME,myuserName);
                startActivity(i);

            }
        });
        DatabaseReference messagequery= rootRef.child("posts").child("public");

        Log.d("TAGGGE","In data snapshot");
        messagequery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Posts message = snapshot.getValue(Posts.class);
                    messagesList.add(message);
                    mAdapter.notifyDataSetChanged();

                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rootRef.child("users").child(uid).child("online").setValue(ServerValue.TIMESTAMP);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rootRef.child("users").child(uid).child("online").setValue(ServerValue.TIMESTAMP);
            }
        }, 15000);

    }


    private void loadMessages()
    {
        DatabaseReference messagequery= rootRef.child("posts").child("public");

        Log.d("TAG","In data snapshot");
        messagequery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("TAG",dataSnapshot.toString());
               /* for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Posts message = snapshot.getValue(Posts.class);
                    messagesList.add(message);
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });/*
        messagequery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Posts message = dataSnapshot.getValue(Posts.class);
                messagesList.add(message);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {            }

            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });
*/
        mAdapter.notifyDataSetChanged();
    }
}
