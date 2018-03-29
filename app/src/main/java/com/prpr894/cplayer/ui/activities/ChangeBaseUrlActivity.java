package com.prpr894.cplayer.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.prpr894.cplayer.MyApp;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.adapters.recycleradapters.ChangeBaseRecyclerAdapter;
import com.prpr894.cplayer.base.BaseActivity;
import com.prpr894.cplayer.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.MyToast;

public class ChangeBaseUrlActivity extends BaseActivity implements ChangeBaseRecyclerAdapter.OnRecyclerItemClickListener {

    private RecyclerView mRecyclerViewChangeBase;
    private String baseServer = "http://ww.jiafangmao.com/";//http://ww.jiafangmao.com/3
    private int page = 0;
    private ChangeBaseRecyclerAdapter mAdapter;

    private int resultCode = Activity.RESULT_CANCELED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_base_url);
        getToolbarTitle().setText("更换直播源");
        initView();
        initData();
    }

    private void initData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(baseServer + i);
        }
        mAdapter = new ChangeBaseRecyclerAdapter(list, this);
        mRecyclerViewChangeBase.setAdapter(mAdapter);
        mRecyclerViewChangeBase.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setOnRecyclerItemClickListener(this);
    }

    private void initView() {
        mRecyclerViewChangeBase = findViewById(R.id.recycler_view_change_base);
        refreshSubtitle();
    }

    private void refreshSubtitle() {
        if (!SPUtil.getBoolen(MyApp.getInstance(), "defaultBase", true)) {
            getSubTitle().setText("恢复默认源");
            getSubTitle().setVisibility(View.VISIBLE);
            getSubTitle().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toDefaltBaseUrlDialog();
                }
            });
        } else {
            getSubTitle().setVisibility(View.GONE);
        }
    }

    private void toDefaltBaseUrlDialog() {

        AlertDialog.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setCancelable(false);
        builder.setTitle("提示");
        builder.setMessage("确定恢复到默认源吗？");
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SPUtil.putBoolen(MyApp.getInstance(), "defaultBase", true);
                mAdapter.toDefaultBaseUrl();
                resultCode = Activity.RESULT_OK;
                getSubTitle().setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        builder.create().show();

    }


    @Override
    public void onRecyclerItemClick(int position, String data, View view) {
        MyToast.successBig("已选择");
        SPUtil.putBoolen(MyApp.getInstance(), "defaultBase", false);
        SPUtil.putString(MyApp.getInstance(), "customBase", data);
        resultCode = Activity.RESULT_OK;
        mAdapter.refreshButton(view, position);
        refreshSubtitle();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(resultCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
