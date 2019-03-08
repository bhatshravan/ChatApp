package inc.bs.chataroo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import inc.bs.chataroo.R;
import inc.bs.chataroo.group_chat.pub.GroupChatPublic;
import inc.bs.chataroo.group_chat.pub.NewGroupChatPublic;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.models.GroupsList;
import inc.bs.chataroo.views.GroupsListViewHolder;

public class GroupsFragment extends Fragment {

    private Toolbar mToolbar;
    com.github.clans.fab.FloatingActionButton ns;

    private RecyclerView mGroupsListList;

    private Query mGroupsListDatabase;


    private LinearLayoutManager mLayoutManager;

    com.github.clans.fab.FloatingActionButton floatingActionButton;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;

    String  uthumb;

    String myname, uid,token,sort;
    private View mMainView;


    public GroupsFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_user_chatgroup, container, false);


        /*ns=mMainView.findViewById(R.id.group_new2);
        ns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), NewGroupChatPublic.class);
                i.putExtra(Constants.FIREBASE_MYUSERNAME, myname);
                i.putExtra(Constants.FIREBASE_USERS_THUMB, uthumb);
                i.putExtra(Constants.FIREBASE_USERS_UID, uid);
                startActivity(i);
            }
        });*/
        Bundle ar = getArguments();
        uid = ar.getString(Constants.FIREBASE_USERS_UID);
        myname = ar.getString(Constants.FIREBASE_MYUSERNAME);
        token = ar.getString(Constants.FIREBASE_MY_FCM_TOKEN);
        uthumb= ar.getString(Constants.FIREBASE_USERS_THUMB);
        sort = getArguments().getString("sort");


        materialDesignFAM = (FloatingActionMenu) mMainView.findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) mMainView.findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) mMainView.findViewById(R.id.material_design_floating_action_menu_item2);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getContext(), NewGroupChatPublic.class);
                i.putExtra(Constants.FIREBASE_MYUSERNAME, myname);
                i.putExtra(Constants.FIREBASE_USERS_THUMB, uthumb);
               i.putExtra("cat", "countries");
                i.putExtra(Constants.FIREBASE_USERS_UID, uid);
                startActivity(i);
            }
        });

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getContext(), NewGroupChatPublic.class);
                i.putExtra(Constants.FIREBASE_MYUSERNAME, myname);
                i.putExtra(Constants.FIREBASE_USERS_THUMB, uthumb);
                i.putExtra("cat", "topics");
                i.putExtra(Constants.FIREBASE_USERS_UID, uid);
                startActivity(i);
            }
        });

       // Log.d("GRO",sort);
        //mGroupsListDatabase = FirebaseDatabase.getInstance().getReference().child("chat").child("group").child("public");

        mGroupsListDatabase = FirebaseDatabase.getInstance().getReference().child("groups").child("public").child("names").orderByChild("category").equalTo(sort);

        mGroupsListDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("GROUPS",dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mLayoutManager = new LinearLayoutManager(getContext());



        mGroupsListList = (RecyclerView) mMainView.findViewById(R.id.users_list2);
        mGroupsListList.setHasFixedSize(true);
        mGroupsListList.setLayoutManager(mLayoutManager);
        return mMainView;
    }


    @Override
    public void onStart() {
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
                usersViewHolder.setUserImage(users.getImage(), getContext());

                final String user_id = getRef(position).getKey();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent i = new Intent(getContext(), GroupChatPublic.class);
                        i.putExtra(Constants.FIREBASE_MYUSERNAME, myname);
                        i.putExtra(Constants.FIREBASE_USERS_THUMB, uthumb);
                        i.putExtra(Constants.FIREBASE_USERS_UID, uid);
                        i.putExtra(Constants.FIREBASE_MY_FCM_TOKEN, token);

                        i.putExtra(Constants.CHAT_GROUP, users.getname());
                        startActivity(i);
                    }
                });

            }
        };

        mGroupsListList.setAdapter(firebaseRecyclerAdapter);

    }
}


