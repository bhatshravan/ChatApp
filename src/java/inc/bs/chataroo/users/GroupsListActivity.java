package inc.bs.chataroo.users;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import inc.bs.chataroo.R;
import inc.bs.chataroo.group_chat.pub.GroupChatPublic;
import inc.bs.chataroo.group_chat.pub.NewGroupChatPublic;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.models.GroupsList;
import inc.bs.chataroo.views.GroupsListViewHolder;

public class GroupsListActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mGroupsListList;

    private DatabaseReference mGroupsListDatabase;

    private LinearLayoutManager mLayoutManager;

    FloatingActionButton floatingActionButton;

    String myname, uid, uthumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_activity_group);

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("All Groups List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGroupsListDatabase = FirebaseDatabase.getInstance().getReference().child("chat").child("group").child("public");

        mLayoutManager = new LinearLayoutManager(this);

        myname = getIntent().getStringExtra(Constants.FIREBASE_MYUSERNAME);
        uthumb = getIntent().getStringExtra(Constants.FIREBASE_USERS_THUMB);
        uid = getIntent().getStringExtra(Constants.FIREBASE_USERS_UID);


        mGroupsListList = (RecyclerView) findViewById(R.id.users_list2);
        mGroupsListList.setHasFixedSize(true);
        mGroupsListList.setLayoutManager(mLayoutManager);

    }

    public void BtnClick(View view) {
        switch (view.getId()) {
            case R.id.group_new:
                Intent i = new Intent(GroupsListActivity.this, NewGroupChatPublic.class);
                i.putExtra(Constants.FIREBASE_MYUSERNAME, myname);
                i.putExtra(Constants.FIREBASE_USERS_THUMB, uthumb);
                i.putExtra(Constants.FIREBASE_USERS_UID, uid);
                startActivity(i);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<GroupsList, GroupsListViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GroupsList, GroupsListViewHolder>(
                GroupsList.class,
                R.layout.users_single_layout,
                GroupsListViewHolder.class,
                mGroupsListDatabase
        )

        {
            @Override
            protected void populateViewHolder(GroupsListViewHolder usersViewHolder, final GroupsList users, int position)
            {
                usersViewHolder.setDisplayName(users.getname());
                usersViewHolder.setUserStatus(users.getDescription());
                usersViewHolder.setUserImage(users.getImage(), getApplicationContext());

                final String user_id = getRef(position).getKey();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent i = new Intent(GroupsListActivity.this, GroupChatPublic.class);
                        i.putExtra(Constants.FIREBASE_MYUSERNAME, myname);
                        i.putExtra(Constants.FIREBASE_USERS_THUMB, uthumb);
                        i.putExtra(Constants.FIREBASE_USERS_UID, uid);
                        i.putExtra(Constants.CHAT_GROUP, users.getname());
                        startActivity(i);
                    }
                });

            }
        };


        mGroupsListList.setAdapter(firebaseRecyclerAdapter);

    }
}


