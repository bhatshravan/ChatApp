package inc.bs.chataroo.activiti;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import inc.bs.chataroo.R;
import inc.bs.chataroo.fragments.GroupsFragment;
import inc.bs.chataroo.misc.Constants;

/**
 * Created by Shravan on 29-01-2018.
 */

public class ChatListActivity extends AppCompatActivity {

    private View mMainView;
    GoogleApiClient mGoogleApiClient;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    InterstitialAd mInterstitialAd;
    String uid,myname,username,userThumb,token;
    AdView mAdView;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragement_menu_chatlist);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myname=getIntent().getStringExtra(Constants.FIREBASE_MYUSERNAME);
        userThumb=getIntent().getStringExtra(Constants.FIREBASE_USERS_THUMB);
        token = getIntent().getStringExtra(Constants.FIREBASE_MY_FCM_TOKEN);
        uid = getIntent().getStringExtra("uid");


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Activity.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter4 adapter = new ViewPagerAdapter4(getSupportFragmentManager(),uid,myname,token);
        adapter.addFrag(new GroupsFragment(), "Topics");
        adapter.addFrag(new GroupsFragment(), "Countries");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter4 extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        String u1;String u2,u3;

        public ViewPagerAdapter4(FragmentManager manager, String u1, String u2, String u3) {
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

    }
}
