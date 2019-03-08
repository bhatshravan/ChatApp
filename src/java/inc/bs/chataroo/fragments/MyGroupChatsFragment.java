package inc.bs.chataroo.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import inc.bs.chataroo.R;
import inc.bs.chataroo.induv_chat.ChatActivity;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.misc.SimpleDividerItemDecoration;
import inc.bs.chataroo.models.MyChats;
import inc.bs.chataroo.views.MyChatsViewHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyGroupChatsFragment extends Fragment {


    private View mMainView;


    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private LinearLayoutManager mLayoutManager;

    String myname, uid,token;
String ll,ll2,ll3;
    public MyGroupChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.users_activity_users_fragment, container, false);

        mLayoutManager = new LinearLayoutManager(getContext());

        Bundle ar = getArguments();
        uid = ar.getString(Constants.FIREBASE_USERS_UID);
        myname = ar.getString(Constants.FIREBASE_MYUSERNAME);
        token = ar.getString(Constants.FIREBASE_MY_FCM_TOKEN);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("chatlists").child("private").child(uid);
        mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mUsersList = (RecyclerView) mMainView.findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);
        mUsersList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        FirebaseRecyclerAdapter<MyChats, MyChatsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MyChats, MyChatsViewHolder>(

                MyChats.class,
                R.layout.users_single_layout,
                MyChatsViewHolder.class,
                mUsersDatabase

        ) {
            @Override
            protected void populateViewHolder(MyChatsViewHolder usersViewHolder, final MyChats users, int position) {


                ll=users.getLast();
//                            if(ll.length()<30)
                usersViewHolder.setUserStatus(users.getLast());
                //                          else
                //                            usersViewHolder.setUserStatus((users.getLast().substring(0,30))+"...");

                usersViewHolder.setDisplayName(users.getUsername());
                usersViewHolder.setUserImage(users.getUid(),getContext());

                final String user_id = getRef(position).getKey();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getActivity(), ChatActivity.class);
                        i.putExtra(Constants.FIREBASE_USERS_UID, users.getUid());
                        i.putExtra(Constants.FIREBASE_USERNAME, users.getUsername());
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
