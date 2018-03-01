package com.prpr894.cplayer.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.baidu.cloud.videoplayer.demo.AdvancedPlayActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.MyToast;

import static com.prpr894.cplayer.utils.AppConfig.PLAY_TYPE_BAI_DU;

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

//        OkGo.<String>head("http://ww.jiafangmao.com/5/xyjk.html")//
//                .tag(this)//可以不用
//                .headers("header1", "headerValue1")//是否可以不用？
//                .params("param1", "paramValue1")////是否可以不用？
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        okhttp3.Response rawResponse = response.getRawResponse();
//                        if (rawResponse != null) {
//                            int code = rawResponse.code();//200为OK
//                        }
//                    }
//                });

        if (!isRefresh) {
            showProgress("加载中...", false);
        }

        String baseUrl;
        if (SPUtil.getBoolen(MyApp.getInstance(), "useServerBase", true)) {
            baseUrl = SPUtil.getString(MyApp.getInstance(), "baseUrlFromServer", "http://ww.jiafangmao.com/6");
        } else {
            baseUrl = "http://ww.jiafangmao.com/6";
        }
        OkGo.<String>get(baseUrl + data.getUrl())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.d("flag", "返回的房间列表： " + response.body());
                        hideProgress();
                        mSmartRefreshLayout.finishRefresh();
                        try {
                            JSONObject object = new JSONObject(response.body());
                            if (object.has("data")) {
                                Gson gson = new Gson();
                                List<LiveRoomItemDataBean> data = gson.fromJson(response.body(), LiveRoomListBean.class).getData();
                                mAdapter.addAll(data);
                            } else {
                                MyToast.error("数据异常！");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MyToast.warn("数据异常！");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        MyToast.error("网络连接异常！");
                        hideProgress();
                        mSmartRefreshLayout.finishRefresh();
                    }
                });
    }

    private void initView() {
        isRefresh = false;
        getToolbarTitle().setText(data.getName());
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
        Intent intent;
        if (SPUtil.getString(MyApp.getInstance(), "payType", PLAY_TYPE_BAI_DU).equals(PLAY_TYPE_BAI_DU)) {
            intent = new Intent(this, AdvancedPlayActivity.class);
        } else {
            intent = new Intent(this, MainPlayerActivity.class);
        }
        Bundle bundle = new Bundle();
        bundle.putString("videoUrl", data.getPlay_url());
        bundle.putString("imgUrl", data.getLogourl());
        bundle.putString("title", data.getNickname());
        bundle.putString("id", data.getUserid());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        isRefresh = true;
        initData();
    }
}
