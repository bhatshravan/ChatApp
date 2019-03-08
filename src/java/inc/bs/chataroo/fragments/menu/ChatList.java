package inc.bs.chataroo.fragments.menu;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import inc.bs.chataroo.MainActivity;
import inc.bs.chataroo.R;
import inc.bs.chataroo.fragments.MyChatsFragment;
import inc.bs.chataroo.fragments.MyGroupChatsFragment;
import inc.bs.chataroo.induv_chat.ChatActivity;
import inc.bs.chataroo.login.ProfileActivity;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.users.UsersActivity;

/**
 * Created by Shravan on 29-01-2018.
 */

public class ChatList extends Fragment {

    private View mMainView;
    GoogleApiClient mGoogleApiClient;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    InterstitialAd mInterstitialAd;
    String uid,myname,username,userThumb,token;
    AdView mAdView;
    private Menu menu;
    boolean us=false;

    public ChatList() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    FloatingActionButton new_pri_chat_fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_menu_chatlist_2, container, false);

        setHasOptionsMenu(true);

        //toolbar = (Toolbar) mMainView.findViewById(R.id.toolbar);
        //((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        myname=getArguments().getString(Constants.FIREBASE_MYUSERNAME);
        userThumb=getArguments().getString(Constants.FIREBASE_USERS_THUMB);
        token = getArguments().getString(Constants.FIREBASE_MY_FCM_TOKEN);
        uid = getArguments().getString("uid");
        //us= getArguments().getBoolean("us");

        viewPager = (ViewPager) mMainView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) mMainView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        new_pri_chat_fab = mMainView.findViewById(R.id.new_pri_btn);


        new_pri_chat_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {/*
                boolean wrapInScrollView = false;
                MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                        .title(R.string.search_person)
                        .customView(R.layout.new_search_view, wrapInScrollView)
                        .positiveText(R.string.search)
                        .negativeText("Cancel", new MaterialDialog.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                            }

                        })
                        .show();

                View positiveAction;

                positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                //noinspection ConstantConditions
                passwordInput = dialog.getCustomView().findViewById(R.id.password);*/
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View promptsView = inflater.inflate(R.layout.new_search_view, null);


                // set prompts.xml to alertdialog builder
                //builder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.search_person);


                builder.setView(promptsView)
                        .setTitle("Search user")
                        // Add action buttons
                        .setPositiveButton(R.string.search_person, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                final String searchP = userInput.getText().toString();

                                DatabaseReference udb = FirebaseDatabase.getInstance().getReference().child("userdata");

                                udb.child(searchP).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild("taken")){

                                            Intent i = new Intent(getContext(), ChatActivity.class);
                                            i.putExtra(Constants.FIREBASE_USERS_UID,dataSnapshot.child("by").getValue().toString());
                                            i.putExtra(Constants.FIREBASE_USERNAME,searchP.toLowerCase().trim());
                                            i.putExtra(Constants.FIREBASE_MYUSERNAME,myname);
                                            i.putExtra(Constants.FIREBASE_FCM_TOKEN,"nope");
                                            startActivity(i);

                                        }
                                        else{
                                            Toast.makeText(getActivity(),"User does not exist",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(getActivity(),"Error contacting database",Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                 builder.create().show();

            }
        });
        return mMainView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService( Activity.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void  onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == R.id.main_settings_btn){
            Intent i = new Intent(getActivity(), ProfileActivity.class);
            i.putExtra(Constants.FIREBASE_MYUSERNAME, username);
            i.putExtra(Constants.FIREBASE_USERS_THUMB, userThumb);
            i.putExtra(Constants.FIREBASE_MY_FCM_TOKEN, token);
            startActivity(i);
        }
        if(item.getItemId() == R.id.main_logout_btn) {
            ((MainActivity)getActivity()).signOut();
        }
        return super.onOptionsItemSelected(item);
    }


    //https://www.reddit.com/user/TheCanadianDoctor/m/imaginary/
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter2 adapter = new ViewPagerAdapter2(getChildFragmentManager(),uid,myname,token);
      /*  if(!us) {
            adapter.addFrag(new FriendsFragment(), "All users");
        }
        else{
        */    adapter.addFrag(new MyChatsFragment(), "Chats");
            //adapter.addFrag(new MyGroupChatsFragment(), "Group");

        //}
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter2 extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        String u1;String u2,u3;

        public ViewPagerAdapter2(FragmentManager manager, String u1, String u2, String u3) {
            super(manager);
            this.u1=u1;
            this.u2=u2;
            this.u3=u3;

        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.FIREBASE_USERS_UID, u1);   //parameters are (key, value).
            bundle.putString(Constants.FIREBASE_MYUSERNAME, u2);  //parameters are (key, value).
            bundle.putString(Constants.FIREBASE_MY_FCM_TOKEN, u3);   //parameters are (key, value).
            fragment.setArguments(bundle);

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //sign out method
    public void signOut() {
        FirebaseAuth.getInstance().signOut();

        /*Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }

                });*/
    }
}
