package inc.bs.chataroo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.fastadapter.utils.RecyclerViewCacheUtil;
import com.mikepenz.materialdrawer.Drawer;

import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Random;

import inc.bs.chataroo.activiti.UpdateActivity;
import inc.bs.chataroo.fragments.FriendsWithBarFragment;
import inc.bs.chataroo.fragments.MyChatsFragment;
import inc.bs.chataroo.fragments.menu.ChatGroup;
import inc.bs.chataroo.fragments.menu.ChatList;
import inc.bs.chataroo.induv_chat.ChatActivity;
import inc.bs.chataroo.login.LoginActivity;
import inc.bs.chataroo.login.ProfileActivity;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.posts.PostsActivity;

public class MainActivity extends AppCompatActivity {

    GoogleApiClient mGoogleApiClient;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    InterstitialAd mInterstitialAd;
    String uid,myname,username,userThumb,token;
    AdView mAdView;
    private AHBottomNavigationAdapter navigationAdapter;
    AHBottomNavigation bottomNavigation;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private int[] tabColors;
    private Menu menu;
    Fragment fr2;
    Bundle ar2;
    Boolean paid=false;
    int ban1,inter1;
    private Toolbar mToolbar;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private Drawer result = null;
    long drawe;
    String con;


    //Change for current version
    int myCurrentVersion = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken("96546229994-a6e8j8lh0s57s8vqg1ssuada22e8m734.apps.googleusercontent.com")
                .requestIdToken(getString(R.string.gso))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(MainActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent ii=getIntent();
        myname=ii.getStringExtra(Constants.FIREBASE_MYUSERNAME);
        userThumb=ii.getStringExtra(Constants.FIREBASE_USERS_THUMB);
        token = getIntent().getStringExtra(Constants.FIREBASE_MY_FCM_TOKEN);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        int l1 = Integer.parseInt(ii.getStringExtra("banner"));

        int l2 = Integer.parseInt(ii.getStringExtra("inter"));


        int upd = Integer.parseInt(ii.getStringExtra("upd"));
        int manupd = Integer.parseInt(ii.getStringExtra("manupd"));


        if(upd>myCurrentVersion)
        {
            Toast.makeText(MainActivity.this,"Please update app within 10 days",Toast.LENGTH_SHORT).show();
        }
        if(manupd>myCurrentVersion)
        {
            startActivity(new Intent(MainActivity.this,UpdateActivity.class));
            finish();
        }


        Random random = new Random();
        if(l1 == 0)
            ban1=0;
        else if(l1==1)
            ban1=1;
        else
            ban1 = random.nextInt(l1);

        if(l2 == 0)
            inter1=0;
        else if(l2==1)
            inter1=1;
        else
            inter1 = random.nextInt(l2);


        showAds(ban1, inter1 );


        Fragment fr = new MyChatsFragment();
        Bundle ar = new Bundle();
        ar.putString(Constants.FIREBASE_MYUSERNAME, myname);
        ar.putString(Constants.FIREBASE_USERS_THUMB, userThumb);
        ar.putString(Constants.FIREBASE_MY_FCM_TOKEN, token);
        ar.putString("uid", uid);
        ar.putBoolean("us",true);

        fr.setArguments(ar);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fr,"home");
        fragmentTransaction.commit();


        if(!isNetworkAvailable())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("No Internet connection. Please check network");
            builder1.setCancelable(true);
            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder1.setPositiveButton(
                    "Reload",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            recreate();
                            dialog.cancel();
                        }
                    }
            );
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHeader(R.layout.nav_header_main)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .withTranslucentStatusBar(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIdentifier(0),
                        new SectionDrawerItem().withDivider(true).withName("Personal chat"),
                        new PrimaryDrawerItem().withName("My Chats").withIdentifier(0),
                        new PrimaryDrawerItem().withName("New Personal Chat").withIdentifier(5),
                        new SectionDrawerItem().withDivider(true).withName("Public chat"),
                        new PrimaryDrawerItem().withName("Public Groups").withIdentifier(1),
                        new PrimaryDrawerItem().withName("Your Groups").withIdentifier(1),
                        new PrimaryDrawerItem().withName("Join Channel").withIdentifier(2),
                        new PrimaryDrawerItem().withName("Create Channel").withIdentifier(2),
                        new SectionDrawerItem().withDivider(true).withName("Other"),
                        new PrimaryDrawerItem().withName("Donate").withIdentifier(10),
                        new PrimaryDrawerItem().withName("Settings").withIdentifier(3)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        drawe = drawerItem.getIdentifier();
                        change((int)drawe,con);
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
        //Pre-cache items to get a better scroll performance
        new RecyclerViewCacheUtil<IDrawerItem>().withCacheSize(2).apply(result.getRecyclerView(), result.getDrawerItems());
        //dialog();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    void showAds(int ban1,int inter1){

        mAdView = (AdView) findViewById(R.id.adView);

        if(!paid) {

            if(ban1==1) {
                AdRequest addRequest = new AdRequest.Builder().addTestDevice("AD1C68BE5E5E567D7D118E3E953AEC7B").build();
                mAdView.loadAd(addRequest);
            }
            else{
                mAdView.setVisibility(View.GONE);
            }

            if(inter1==1) {
                mInterstitialAd = new InterstitialAd(getApplicationContext());
                // set the ad unit ID
                mInterstitialAd.setAdUnitId(getString(R.string.ads_inter1));
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice("AD1C68BE5E5E567D7D118E3E953AEC7B")
                        .build();


                // Load ads into Interstitial Ads
                mInterstitialAd.loadAd(adRequest);
                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        showInterstitialAd();
                    }
                });
            }
        }
        else {
            mAdView.setVisibility(View.GONE);
        }
    }

    private void showInterstitialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Activity.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

 //sign out method
    public void signOut() {
        FirebaseAuth.getInstance().signOut();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }

    @Override
    public boolean  onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return  true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == R.id.main_settings_btn){
            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            i.putExtra(Constants.FIREBASE_MYUSERNAME, username);
            i.putExtra(Constants.FIREBASE_USERS_THUMB, userThumb);
            i.putExtra(Constants.FIREBASE_MY_FCM_TOKEN, token);
            startActivity(i);
        }
        if(item.getItemId() == R.id.main_logout_btn) {
            signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    EditText userInput;

    public void change(int n,String con) {
        switch (n) {
            case 0:
                Fragment fr = new MyChatsFragment();
                Bundle ar = new Bundle();
                ar.putString(Constants.FIREBASE_MYUSERNAME, myname);
                ar.putString(Constants.FIREBASE_USERS_THUMB, userThumb);
                ar.putString(Constants.FIREBASE_MY_FCM_TOKEN, token);
                ar.putString("uid", uid);
                ar.putBoolean("us",true);

                fr.setArguments(ar);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fr,"home");
                fragmentTransaction.commit();

                break;
            case 1:
                Fragment fr2 = new ChatGroup();
                Bundle ar2 = new Bundle();
                ar2.putString(Constants.FIREBASE_MYUSERNAME, myname);
                ar2.putString(Constants.FIREBASE_USERS_THUMB, userThumb);
                ar2.putString(Constants.FIREBASE_MY_FCM_TOKEN, token);
                ar2.putString("uid", uid);

                fr2.setArguments(ar2);
                FragmentManager fragmentManager2 = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                fragmentTransaction2.replace(R.id.container_body, fr2,"home");
                fragmentTransaction2.commit();

                break;
            case 2:
                Fragment fr3 = new FriendsWithBarFragment();
                Bundle ar3 = new Bundle();
                ar3.putString(Constants.FIREBASE_MYUSERNAME, myname);
                ar3.putString(Constants.FIREBASE_USERS_THUMB, userThumb);
                ar3.putString(Constants.FIREBASE_MY_FCM_TOKEN, token);
                ar3.putString("uid", uid);
                ar3.putBoolean("us",false);

                fr3.setArguments(ar3);
                FragmentManager fragmentManager3 = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
                fragmentTransaction3.replace(R.id.container_body, fr3,"home");
                fragmentTransaction3.commit();
                break;
            case 4:

                Intent i2 = new Intent(MainActivity.this, PostsActivity.class);
                i2.putExtra(Constants.FIREBASE_MYUSERNAME, username);
                i2.putExtra(Constants.FIREBASE_USERS_THUMB, userThumb);
                i2.putExtra(Constants.FIREBASE_MY_FCM_TOKEN, token);
                i2.putExtra("uid", uid);
                startActivity(i2);
                break;
            case 3:

                Intent i3 = new Intent(MainActivity.this, inc.bs.chataroo.login.Status.class);
                i3.putExtra(Constants.FIREBASE_MYUSERNAME, username);
                i3.putExtra(Constants.FIREBASE_USERS_THUMB, userThumb);
                i3.putExtra(Constants.FIREBASE_MY_FCM_TOKEN, token);
                i3.putExtra("uid", uid);
                startActivity(i3);
                break;

            case 5:
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);// Get the layout inflater
                LayoutInflater inflater = getLayoutInflater();
                View promptsView = inflater.inflate(R.layout.new_search_view, null);
                userInput = (EditText) promptsView
                        .findViewById(R.id.search_person);
                builder.setView(promptsView)
                        .setTitle("Search user")
                        .setPositiveButton(R.string.search_person, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                final String searchP = userInput.getText().toString();
                                DatabaseReference udb = FirebaseDatabase.getInstance().getReference().child("userdata");

                                udb.child(searchP).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild("taken")){

                                            Intent i = new Intent(MainActivity.this, ChatActivity.class);
                                            i.putExtra(Constants.FIREBASE_USERS_UID,dataSnapshot.child("by").getValue().toString());
                                            i.putExtra(Constants.FIREBASE_USERNAME,searchP.toLowerCase().trim());
                                            i.putExtra(Constants.FIREBASE_MYUSERNAME,myname);
                                            i.putExtra(Constants.FIREBASE_FCM_TOKEN,"nope");
                                            startActivity(i);

                                        }
                                        else{
                                            Toast.makeText(MainActivity.this,"User does not exist",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(MainActivity.this,"Error contacting database",Toast.LENGTH_SHORT).show();
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
    }


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