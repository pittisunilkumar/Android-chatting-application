package com.example.gochats.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.gochats.CallsFragment;
import com.example.gochats.ChatsFragment;
import com.example.gochats.StatusFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter {


    public TabsAccessorAdapter(@NonNull FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {

        switch (i)
        {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                StatusFragment Statusfragment = new StatusFragment();
                return Statusfragment;
            case 2:
                CallsFragment callsFragment = new CallsFragment();
                return callsFragment;
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
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Messages";
            case 1:
                return "Storys";
            case 2:
                return "Calls";
            default:
                return null;
        }
    }
}
