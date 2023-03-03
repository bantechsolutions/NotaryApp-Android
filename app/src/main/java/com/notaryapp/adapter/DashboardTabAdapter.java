package com.notaryapp.adapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DashboardTabAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> titleList = new ArrayList<>();
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    private String mSearchTerm;
    //integer to count number of tabs
    int tabCount;

    public DashboardTabAdapter(@NonNull FragmentManager fm,int tabCount, String searchTerm) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
        this.mSearchTerm= searchTerm;
    }
    //FragmentManager fm,
    @Override
    public int getCount() {
        return titleList.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                Dashboard_TabAllFragment tab1 = Dashboard_TabAllFragment.newInstance(mSearchTerm);
//                return tab1;
//            case 1:
//                Dashboard_TabValidateFragment tab2 = Dashboard_TabValidateFragment.newInstance(mSearchTerm);
//                return tab2;
//            case 2:
//                Dashboard_TabAuthFragment tab3 = Dashboard_TabAuthFragment.newInstance(mSearchTerm);
//                return tab3;
//            default:
//                return null;
//        }
        return fragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    public void AddFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        titleList.add(title);
    }

    @NotNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
