package com.prpr894.cplayer.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.prpr894.cplayer.MyApp;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.adapters.recycleradapters.RoomRecyclerAdapter;
import com.prpr894.cplayer.base.BaseActivity;
import com.prpr894.cplayer.bean.LiveRoomItemDataBean;
import com.prpr894.cplayer.bean.LiveRoomListBean;
import com.prpr894.cplayer.bean.StationListItemDataBean;
import com.prpr894.cplayer.utils.SPUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.MyToast;

public class LiveRoomListActivity extends BaseActivity implements RoomRecyclerAdapter.OnRecyclerItemClickListener, OnRefreshListener {
    private StationListItemDataBean data;
    private List<LiveRoomItemDataBean> mList;
    private RecyclerView mRecyclerView;
    private RoomRecyclerAdapter mAdapter;
    private SmartRefreshLayout mSmartRefreshLayout;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room_list);
        initBundle();
        initView();
        initData();
    }

    private void initData() {
        if (!isRefresh) {
            showProgress("加载中...", false);
        }

        String baseUrl;
        if (SPUtil.getBoolen(MyApp.getInstance(), "defaultBase", true)) {
            baseUrl = SPUtil.getString(MyApp.getInstance(), "baseUrlFromServer", "http://api.hclyz.cn:81/mf/");
        } else {
            baseUrl = SPUtil.getString(MyApp.getInstance(), "customBase", "http://api.hclyz.cn:81/mf/");
        }
        final String baseUrlStr = baseUrl;
        OkGo.<String>get(baseUrl + data.getAddress())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.d("flag", "返回的房间列表： " + response.body());
                        hideProgress();
                        mSmartRefreshLayout.finishRefresh();
                        Gson gson = new Gson();
                        List<LiveRoomItemDataBean> data = gson.fromJson(response.body(), LiveRoomListBean.class).getZhubo();
                        mAdapter.removeAll();
                        mAdapter.addAll(data);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.d("flag", baseUrlStr + " code: " + response.code());
                        MyToast.error("网络连接异常！");
                        hideProgress();
                        mSmartRefreshLayout.finishRefresh();
                    }
                });
    }

    private void initView() {
        isRefresh = false;
        getToolbarTitle().setText(data.getTitle());
        Log.d("flag", data.toString());

        mSmartRefreshLayout = findViewById(R.id.sm_live_room_list);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setOnRefreshListener(this);

        mList = new ArrayList<>();
        mAdapter = new RoomRecyclerAdapter(mList, this);
        mAdapter.setOnRecyclerItemClickListener(this);
        mRecyclerView = findViewById(R.id.recycler_view_room_list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));


    }

    private void initBundle() {
        data = ((StationListItemDataBean) Objects.requireNonNull(getIntent().getExtras()).getSerializable("data"));
    }

    @Override
    public void onRecyclerItemClick(RecyclerView recycler, View view, int position, long id, LiveRoomItemDataBean data) {
        mAdapter.playNow(data);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        isRefresh = true;
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.checkCollectionState();
    }
}
