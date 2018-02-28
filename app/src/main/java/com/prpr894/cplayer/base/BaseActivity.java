package com.prpr894.cplayer.base;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.prpr894.cplayer.R;

/**
 * Created by dell on 2018/1/2 0002.
 */

public class BaseActivity extends AppCompatActivity implements BaseInterface {
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private TextView mToolbarSubTitle;
    public ProgressDialog progressDialog;

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /** 初始化Toolbar*/
        mToolbar = findViewById(R.id.toolbar);
        mToolbarTitle = findViewById(R.id.toolbar_title);
        mToolbarSubTitle = findViewById(R.id.toolbar_subtitle);
        if (mToolbar != null) {
            //将Toolbar显示到界面
            setSupportActionBar(mToolbar);
            mToolbarSubTitle.setVisibility(View.GONE);
        }
        if (mToolbarTitle != null) {
            //getTitle()的值是activity的android:lable属性值
            mToolbarTitle.setText(getTitle());
            //设置默认的标题不显示
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }


    @Override
    public void finishUI() {
        finish();
    }

    @Override
    public void showProgress(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    public void showProgress(String msg, boolean flag) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(flag);
        progressDialog.setCanceledOnTouchOutside(flag);
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 判断是否有Toolbar,并默认显示返回按钮
         */
        if (null != getToolbar() && isShowBacking()) {
            showBack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    /**
     * 获取头部标题的TextView
     *
     * @return
     */
    public TextView getToolbarTitle() {
        return mToolbarTitle;
    }

    /**
     * 获取副标题的TextView
     *
     * @return
     */
    public TextView getSubTitle() {
        return mToolbarSubTitle;
    }

    /**
     * 设置主标题
     *
     * @param title
     */
    public void setToolBarTitle(CharSequence title) {
        if (mToolbarTitle != null) {
            mToolbarTitle.setText(title);
        } else {
            getToolbar().setTitle(title);
            setSupportActionBar(getToolbar());
        }
    }

    /**
     * this Activity of tool bar.
     * 获取头部.
     *
     * @return support.v7.widget.Toolbar.
     */
    public Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    /**
     * 版本号小于21的后退按钮图片
     */
    private void showBack() {
        //setNavigationIcon必须在setSupportActionBar(toolbar);方法后面加入
        getToolbar().setNavigationIcon(R.drawable.ic_back);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 是否显示标题
     */
    protected void isShowingTitle(boolean isShow) {
        if (isShow) {
            mToolbarTitle.setVisibility(View.VISIBLE);
        } else {
            mToolbarTitle.setVisibility(View.GONE);
        }

    }

    /**
     * 是否显示副标题
     */
    protected void isShowingSubTitle(boolean isShow) {
        if (isShow) {
            mToolbarSubTitle.setVisibility(View.VISIBLE);
        } else {
            mToolbarSubTitle.setVisibility(View.GONE);
        }
    }

    /**
     * 是否显示后退按钮,默认显示,可在子类重写该方法.
     *
     * @return
     */
    protected boolean isShowBacking() {
        return true;
    }
}
