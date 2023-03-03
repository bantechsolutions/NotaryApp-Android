package com.notaryapp.ui.activities.notaryapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.notaryapp.ui.activities.notaryapp.fragments.TS_cancelled_fragments;
import com.notaryapp.ui.activities.notaryapp.fragments.TS_paying_fragments;

public class TSViewPageAdapter extends FragmentPagerAdapter {

    public TSViewPageAdapter(FragmentManager fm){
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new TS_paying_fragments();
        }
        else if (position == 1)
        {
            fragment = new TS_cancelled_fragments();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position){
        String title = null;
        if (position == 0)
        {
            title = "Paying";
        }
        else if (position == 1)
        {
            title = "Cancelled";
        }
        /*else if (position == 2)
        {
            title = "Tab-3";
        }*/
        return title;
    }
}
