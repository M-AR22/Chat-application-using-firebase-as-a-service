package com.mayankarora.chitchat;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectionPagerAdapter extends FragmentPagerAdapter {
    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                ChatsFragment chatfragment=new ChatsFragment();
                return chatfragment;
            case 1:
                RequestFragment requestfragment=new RequestFragment();
                return requestfragment;
            case 2:
                FriendsFragment friendsfragmnet=new FriendsFragment();
                return friendsfragmnet;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "CHATS";
            case 1:
                return "REQUESTS";
            case 2:
                return "FRIENDS";
            default:
                return null;
        }
    }
}
