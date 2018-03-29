package com.prpr894.cplayer.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.prpr894.cplayer.MyApp;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.base.BaseActivity;
import com.prpr894.cplayer.greendao.gen.LiveRoomItemDataBeanDao;
import com.prpr894.cplayer.ui.fragments.DownloadManagerFragment;
import com.prpr894.cplayer.ui.fragments.LiveStationListFragment;
import com.prpr894.cplayer.ui.fragments.LocalMusicFragment;
import com.prpr894.cplayer.ui.fragments.LocalVideoFragment;
import com.prpr894.cplayer.ui.fragments.MainPageLiveFragment;
import com.prpr894.cplayer.ui.fragments.SearchMusicFragment;
import com.prpr894.cplayer.utils.SPUtil;
import com.prpr894.cplayer.view.CustomDrawerLayout;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

import es.dmoral.toasty.MyToast;

import static com.prpr894.cplayer.utils.AppConfig.CODE_PERMISSION_SETTING;
import static com.prpr894.cplayer.utils.AppConfig.EXIT_NOTIFICATION_DIALOG;

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
        //此处用来检查权限，判断运行系统SDK版本是否大于等于23（android6.0）
        rxPermissions = new RxPermissions(this);
        initReasionDialog();
        initPermissionSettingDialog();
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissionNow();
        }
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
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        AlertDialog.Builder builder;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(MainActivity.this);
                        }
                        builder.setCancelable(false);
                        builder.setTitle("警告");
                        builder.setMessage("清空数据不可恢复，确定清空收藏吗？");
                        builder.setPositiveButton("清空", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                LiveRoomItemDataBeanDao beanDao = MyApp.getInstance().getDaoSession().getLiveRoomItemDataBeanDao();
                                beanDao.deleteAll();
                                MyToast.successBig("清除成功");
                            }
                        });

                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.create().show();
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection, menu);
        return true;
    }

    //刷新menu的选项
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCurrentFrg.getClass().getSimpleName().equals(MainPageLiveFragment.class.getSimpleName())) {
            if (MainPageLiveFragment.getCurrentPage() == 1) {
                menu.findItem(R.id.action_settings).setVisible(true);
            } else {
                menu.findItem(R.id.action_settings).setVisible(false);
            }
        } else {
            menu.findItem(R.id.action_settings).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
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
        //刷新menu
        invalidateOptionsMenu();
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
                startActivityForResult(intentChangeBase, LiveStationListFragment.CHANGE_CODE);
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
        if (mCustomDrawerLayout.isDrawerOpen(mNavigationView)) {
            mCustomDrawerLayout.closeDrawers();
            return;
        }
        if (SPUtil.getBoolen(MyApp.getInstance(), EXIT_NOTIFICATION_DIALOG, true)) {
            showDialogExit();
        } else {
            if (System.currentTimeMillis() - isExitTime >= 2000) {
                MyToast.info("再次点击退出程序");
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
        builder.setNeutralButton("退出并不再提示", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SPUtil.putBoolen(MyApp.getInstance(), EXIT_NOTIFICATION_DIALOG, false);
                ActivityCompat.finishAffinity(MainActivity.this);
            }
        });

        builder.create().show();

    }


    //运行时权限检查

    RxPermissions rxPermissions;
    private AlertDialog.Builder mBuilder;
    private AlertDialog.Builder mBuilderSetting;
    private AlertDialog mAlertDialog;
    private AlertDialog mAlertDialogSettings;
    private boolean settingsFlag = false;

    @SuppressLint("CheckResult")
    private void checkPermissionNow() {
        rxPermissions
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new io.reactivex.functions.Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (permission.granted) {
                            // 已经全部允许了,继续其他操作
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 有拒绝的，也有没拒绝的，会走这里，直到全部允许

                            // 展示对话框
                            if (!mAlertDialog.isShowing()) {
                                mAlertDialog.show();
                            }
                        } else {
                            //拒绝了并且不再允许会走这里，需要去设置里更改
                            //写个Dialog，是否跳转，是就跳，否就结束程序

                            // 展示对话框
                            if (!mAlertDialogSettings.isShowing()) {
                                mAlertDialogSettings.show();
                            }
                        }

                    }
                });
    }

    private void initPermissionSettingDialog() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            mBuilderSetting = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        } else {
            mBuilderSetting = new AlertDialog.Builder(this);
        }
        mBuilderSetting.setTitle("需要权限");
        mBuilderSetting.setMessage("跳转至设置更改权限");
        mBuilderSetting.setIcon(R.mipmap.ic_launcher);
        mBuilderSetting.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //跳转至设置界面
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(localIntent);
                settingsFlag = true;
            }
        });
        mBuilderSetting.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyToast.error("未获取权限，退出程序");
                ActivityCompat.finishAffinity(MainActivity.this);
            }
        });
        mBuilderSetting.setCancelable(false);
        mAlertDialogSettings = mBuilderSetting.create();
    }

    private void initReasionDialog() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            mBuilder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        } else {
            mBuilder = new AlertDialog.Builder(this);
        }
        mBuilder.setTitle("需要权限");
        mBuilder.setMessage("截图等功能需要内存卡读写权限以正常运行");
        mBuilder.setIcon(R.mipmap.ic_launcher);
        mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                checkPermissionNow();
            }
        });
        mBuilder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyToast.error("未获取权限，退出程序");
                ActivityCompat.finishAffinity(MainActivity.this);
            }
        });
        mBuilder.setCancelable(false);
        mAlertDialog = mBuilder.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsFlag) {
            settingsFlag = false;
            checkPermissionNow();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("flag", "走了MainActivity onActivityResult");
        switch (resultCode) {
            case RESULT_CANCELED:
                Log.d("flag", "走了MainActivity onActivityResult RESULT_CANCELED");
                break;
            case RESULT_OK:
                Log.d("flag", "requestCode: " + requestCode);
                if (requestCode == LiveStationListFragment.CHANGE_CODE) {
                    if (mCurrentFrg.getClass().getSimpleName().equals(MainPageLiveFragment.class.getSimpleName())) {
                        if (MainPageLiveFragment.getCurrentPage() == 0) {
                            Log.d("flag", "走了MainActivity onActivityResult === MainPageLiveFragment.getCurrentPage() == 0");
                            LiveStationListFragment liveStationListFragment = ((MainPageLiveFragment) mCurrentFrg).getLiveStationListFragment();
                            if (liveStationListFragment != null) {
                                liveStationListFragment.onRefreshNow();
                            }
                        }
                    }
                }
                Log.d("flag", "走了MainActivity onActivityResult RESULT_OK");
                break;
        }
    }
}
