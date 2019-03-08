package inc.bs.chataroo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import inc.bs.chataroo.misc.Constants;

/**
 * Created by AkshayeJH on 11/06/17.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter{


    String u1,u2;
    public SectionsPagerAdapter(FragmentManager fm,String u1,String u2) {
        super(fm);
        this.u1=u1;
        this.u2=u2;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.FIREBASE_USERS, u1);   //parameters are (key, value).
        bundle.putString(Constants.FIREBASE_USERS_THUMB, u2);   //parameters are (key, value).

        FriendsFragment friendsFragment = new FriendsFragment();
        friendsFragment.setArguments(bundle);
        return friendsFragment;
/*
        switch(position) {
            case 0:
                RequestsFragment requestsFragment = new RequestsFragment();
                requestsFragment.setArguments();
                return requestsFragment;

            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return  chatsFragment;

            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            default:
                return  null;
        }
*/
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "GROUPS";

            case 1:
                return "PRIVATE";

            case 2:
                return "POSTS";

            default:
                return null;
        }

    }

}
