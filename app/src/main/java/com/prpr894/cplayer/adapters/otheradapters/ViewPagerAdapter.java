package com.prpr894.cplayer.adapters.otheradapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by ChenShuo on 2017/12/7.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private String tabTitles[] = new String[1];

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }
    public ViewPagerAdapter(FragmentManager manager, String[] titles) {
        super(manager);
        tabTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.length > position ? tabTitles[position] : "";
    }
}
