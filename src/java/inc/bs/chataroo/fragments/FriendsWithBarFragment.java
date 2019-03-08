package inc.bs.chataroo.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import inc.bs.chataroo.R;
import inc.bs.chataroo.induv_chat.ChatActivity;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.misc.SimpleDividerItemDecoration;
import inc.bs.chataroo.models.Users;
import inc.bs.chataroo.views.UsersViewHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsWithBarFragment extends Fragment {


    private View mMainView;


    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private LinearLayoutManager mLayoutManager;

    String myname, uid,token;

    public FriendsWithBarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends_with_bar, container, false);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        /*Toolbar toolbar = (Toolbar) mMainView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Chataroo");
        */

        mLayoutManager = new LinearLayoutManager(getContext());

        Bundle ar = getArguments();
        uid = ar.getString(Constants.FIREBASE_USERS_UID);
        myname = ar.getString(Constants.FIREBASE_MYUSERNAME);
        token = ar.getString(Constants.FIREBASE_MY_FCM_TOKEN);


        mUsersList = (RecyclerView) mMainView.findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);
        mUsersList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));


        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
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
                usersViewHolder.setUserImage(users.getImage_thumbnail(), getActivity());

                final String user_id = getRef(position).getKey();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getActivity(), ChatActivity.class);
                        i.putExtra(Constants.FIREBASE_USERS_UID, users.getUid());
                        i.putExtra(Constants.FIREBASE_USERNAME, users.getusername());
                        i.putExtra(Constants.FIREBASE_MYUSERNAME, myname);
                        i.putExtra(Constants.FIREBASE_MY_FCM_TOKEN, token);
                        i.putExtra(Constants.FIREBASE_FCM_TOKEN, users.getToken());

                        startActivity(i);
                    }
                });
            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }
}
