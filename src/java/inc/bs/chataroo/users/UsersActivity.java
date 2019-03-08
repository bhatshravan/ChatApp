package inc.bs.chataroo.users;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import inc.bs.chataroo.R;
import inc.bs.chataroo.induv_chat.ChatActivity;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.models.Users;
import inc.bs.chataroo.views.UsersViewHolder;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private LinearLayoutManager mLayoutManager;

    String myname,uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_activity_users);

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mLayoutManager = new LinearLayoutManager(this);

        uid = getIntent().getStringExtra(Constants.FIREBASE_USERS_UID);
        myname = getIntent().getStringExtra(Constants.FIREBASE_MYUSERNAME);



        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, final Users users, int position) {

                usersViewHolder.setDisplayName(users.getusername());
                usersViewHolder.setUserStatus(users.getStatus());
                usersViewHolder.setUserImage(users.getImage(), getApplicationContext());

                final String user_id = getRef(position).getKey();
                Log.d("Useer",users.getImage_thumbnail());

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    /*    Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);
*/
                        Intent i = new Intent(UsersActivity.this, ChatActivity.class);
                        i.putExtra(Constants.FIREBASE_USERS_UID,users.getUid());
                        i.putExtra(Constants.FIREBASE_USERNAME,users.getusername());
                        i.putExtra(Constants.FIREBASE_MYUSERNAME,myname);

                        startActivity(i);
                    }
                });

            }
        };


        mUsersList.setAdapter(firebaseRecyclerAdapter);

    }



}
