package com.prpr894.cplayer.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.prpr894.cplayer.MyApp;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.base.BaseActivity;
import com.prpr894.cplayer.ui.fragments.DownloadManagerFragment;
import com.prpr894.cplayer.ui.fragments.LiveStationListFragment;
import com.prpr894.cplayer.ui.fragments.LocalMusicFragment;
import com.prpr894.cplayer.ui.fragments.LocalVideoFragment;
import com.prpr894.cplayer.ui.fragments.MainPageLiveFragment;
import com.prpr894.cplayer.ui.fragments.SearchMusicFragment;
import com.prpr894.cplayer.utils.SPUtil;
import com.prpr894.cplayer.view.CustomDrawerLayout;

import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.MyToast;

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
            } else if (key == 2) {
                mShowFrg = new LocalMusicFragment();
                frgMap.put(key, mShowFrg);
            } else if (key == 3) {
                mShowFrg = new SearchMusicFragment();
                frgMap.put(key, mShowFrg);
            } else if (key == 4) {
                mShowFrg = new DownloadManagerFragment();
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
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                mCustomDrawerLayout.closeDrawers();
                break;
            case R.id.m_change_base:
                Intent intentChangeBase = new Intent(this, ChangeBaseUrlActivity.class);
                startActivity(intentChangeBase);
                mCustomDrawerLayout.closeDrawers();
                break;
            case R.id.m_theme:
                mCustomDrawerLayout.closeDrawers();
                MyToast.info("暂未实现");
                break;
            case R.id.m_about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                mCustomDrawerLayout.closeDrawers();
                break;
            case R.id.m_exit_app:
                ActivityCompat.finishAffinity(this);
                break;
            case R.id.s_live:
                switchMainPage(item, 0);
                break;
            case R.id.s_local_video:
                switchMainPage(item, 1);
                break;
            case R.id.s_local_music:
                switchMainPage(item, 2);
                break;
            case R.id.s_search_music:
                switchMainPage(item, 3);
                break;
            case R.id.s_download_manager:
                switchMainPage(item, 4);
                break;

        }
        return true;
    }

    private void switchMainPage(@NonNull MenuItem item, int i) {
        mToolbar.setTitle(item.getTitle().toString());
        switchPage(i);
        mCustomDrawerLayout.closeDrawers();
    }

    private long isExitTime = 0;

    @Override
    public void onBackPressed() {
        if (SPUtil.getBoolen(MyApp.getInstance(), "exitNotification", true)) {
            showDialogExit();
        } else {
            if (System.currentTimeMillis() - isExitTime >= 2000) {
                MyToast.warn("再次点击退出程序");
                isExitTime = System.currentTimeMillis();
            } else {
                ActivityCompat.finishAffinity(MainActivity.this);
            }
        }
    }

    private void showDialogExit() {
        AlertDialog.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("提示");
        builder.setMessage("确定退出程序吗？");
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.finishAffinity(MainActivity.this);
            }
        });
        builder.setNeutralButton("不再提示", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SPUtil.putBoolen(MyApp.getInstance(), "exitNotification", false);
            }
        });

        builder.create().show();

    }
}
