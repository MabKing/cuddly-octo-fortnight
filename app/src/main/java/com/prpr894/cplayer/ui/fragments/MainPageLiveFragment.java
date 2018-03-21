package com.prpr894.cplayer.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prpr894.cplayer.R;
import com.prpr894.cplayer.adapters.otheradapters.ViewPagerAdapter;
import com.prpr894.cplayer.base.BaseFragment;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainPageLiveFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private static ViewPager mViewPager;

    public MainPageLiveFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page_live, container, false);
        ViewPager viewPager = view.findViewById(R.id.page);
        mViewPager = viewPager;
        TabLayout tabLayout = view.findViewById(R.id.tab);
        String[] title = {"直播", "收藏"};
        ViewPagerAdapter adapter = new ViewPagerAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), title);
        adapter.addFragment(new LiveStationListFragment());
        adapter.addFragment(new CollectionsFragment());
        viewPager.setAdapter(adapter);
        //让ViewPager缓存2个页面
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
        return view;
    }

    public static int getCurrentPage() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Objects.requireNonNull(getActivity()).invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
