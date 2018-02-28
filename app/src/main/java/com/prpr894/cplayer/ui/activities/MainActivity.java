package com.prpr894.cplayer.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.prpr894.cplayer.R;
import com.prpr894.cplayer.base.BaseActivity;
import com.prpr894.cplayer.ui.fragments.LocalVideoFragment;
import com.prpr894.cplayer.ui.fragments.MainPageLiveFragment;
import com.prpr894.cplayer.view.CustomDrawerLayout;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    private CustomDrawerLayout mCustomDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryGray));
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar_main);
        mToolbar.setTitle("直播");
        mCustomDrawerLayout = findViewById(R.id.drawer);
        //注意此段顺序
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mCustomDrawerLayout, mToolbar, 0, 0);//不能挪走，应在上面两行的下面
        mCustomDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mNavigationView = findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(this);
        //开启彩色图标
        mNavigationView.setItemIconTintList(null);
        //初始化一些东西
        mNavigationView.setCheckedItem(R.id.s_live);
        switchPage(0);
    }

    /**
     * 切换fragment
     */
    private Fragment mCurrentFrg;//当前显示fragment
    private Fragment mShowFrg;//将要显示的fragment
    private HashMap<Integer, Fragment> frgMap;
    FragmentManager frgManage = null;//fragment管理器

    @SuppressLint("UseSparseArrays")
    public void switchPage(int key) {

        if (frgMap == null) {
            frgMap = new HashMap<>();
        }

        mShowFrg = frgMap.get(key);
        if (mShowFrg == null) {
            if (key == 0) {
                mShowFrg = new MainPageLiveFragment();
                frgMap.put(key, mShowFrg);
            } else if (key == 1) {
                mShowFrg = new LocalVideoFragment();
                frgMap.put(key, mShowFrg);
            }
        }
        if (frgManage == null) {
            frgManage = getSupportFragmentManager();
        }
        FragmentTransaction transaction = frgManage.beginTransaction();
        //fragment的显示隐藏
        if (!mShowFrg.isAdded()) {//fragment是否被添加过
            if (mCurrentFrg == null) {
                transaction.add(R.id.layout_main_content, mShowFrg).commit();
            } else {
                transaction.add(R.id.layout_main_content, mShowFrg).hide(mCurrentFrg).show(mShowFrg).commit();
            }
        } else {
            transaction.hide(mCurrentFrg).show(mShowFrg).commit();
        }
        mCurrentFrg = mShowFrg;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.m_2:
                mCustomDrawerLayout.closeDrawers();
                break;
            case R.id.m_3:
                mCustomDrawerLayout.closeDrawers();
                break;
            case R.id.m_4:
                ActivityCompat.finishAffinity(this);
                break;
            case R.id.s_live:
                mToolbar.setTitle(item.getTitle().toString());
                switchPage(0);
                mCustomDrawerLayout.closeDrawers();
                break;
            case R.id.s_local_video:
                mToolbar.setTitle(item.getTitle().toString());
                switchPage(1);
                mCustomDrawerLayout.closeDrawers();
                break;

        }
        return true;
    }
}
