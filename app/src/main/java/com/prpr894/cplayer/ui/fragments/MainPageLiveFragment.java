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

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainPageLiveFragment extends Fragment {


    public MainPageLiveFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page_live, container, false);
        ViewPager viewPager = view.findViewById(R.id.page);
        TabLayout tabLayout = view.findViewById(R.id.tab);
        String[] title = {"直播","收藏"};
        ViewPagerAdapter adapter = new ViewPagerAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),title);
        adapter.addFragment(new LiveStationListFragment());
        adapter.addFragment(new CollectionsFragment());
        viewPager.setAdapter(adapter);
        //让ViewPager缓存2个页面
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

}
