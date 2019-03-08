package inc.bs.chataroo.fragments.menu;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

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

public class ChatGroup extends Fragment {

    private View mMainView;
    GoogleApiClient mGoogleApiClient;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    InterstitialAd mInterstitialAd;
    String uid,myname,username,userThumb,token,sort;
    AdView mAdView;
    private Menu menu;

    public ChatGroup() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_menu_chatlist_2, container, false);

        setHasOptionsMenu(true);

        toolbar = (Toolbar) mMainView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        myname=getArguments().getString(Constants.FIREBASE_MYUSERNAME);
        userThumb=getArguments().getString(Constants.FIREBASE_USERS_THUMB);
        token = getArguments().getString(Constants.FIREBASE_MY_FCM_TOKEN);
        uid = getArguments().getString("uid");
        sort = getArguments().getString("sort");


        viewPager = (ViewPager) mMainView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) mMainView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return mMainView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService( Activity.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter3 adapter = new ViewPagerAdapter3(getChildFragmentManager(),uid,myname,token);
        adapter.addFrag(new GroupsFragment(), "Topics");
        adapter.addFrag(new GroupsFragment(), "Countries");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter3 extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        String u1;String u2,u3;


        public ViewPagerAdapter3(FragmentManager manager, String u1, String u2, String u3) {
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
            bundle.putString(Constants.FIREBASE_MY_FCM_TOKEN, u3);//parameters are (key, value).
            bundle.putString("sort", title.toLowerCase());
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
