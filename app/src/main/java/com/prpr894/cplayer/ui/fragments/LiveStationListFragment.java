package com.prpr894.cplayer.ui.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lzy.okgo.utils.IOUtils;
import com.prpr894.cplayer.MyApp;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.adapters.recycleradapters.StationRecyclerAdapter;
import com.prpr894.cplayer.base.BaseFragment;
import com.prpr894.cplayer.bean.StationBean;
import com.prpr894.cplayer.bean.StationListItemDataBean;
import com.prpr894.cplayer.ui.activities.ChangeBaseUrlActivity;
import com.prpr894.cplayer.ui.activities.LiveRoomListActivity;
import com.prpr894.cplayer.utils.SPUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveStationListFragment extends BaseFragment implements OnRefreshListener, StationRecyclerAdapter.OnRecyclerItemClickListener {

    private List<StationListItemDataBean> mList;
    private RecyclerView mRecyclerView;
    private StationRecyclerAdapter mAdapter;
    private SmartRefreshLayout mSmartRefreshLayout;
    private boolean isRefresh = false;
    public static final int CHANGE_CODE = 5;

    public LiveStationListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_station_list, container, false);
        initView(view);
        isRefresh = false;
        initData();
        return view;
    }

    private void initView(View view) {
        mSmartRefreshLayout = view.findViewById(R.id.sm_main_station);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mList = new ArrayList<>();
        mAdapter = new StationRecyclerAdapter(mList, getContext());
        mAdapter.setOnRecyclerItemClickListener(this);
        mRecyclerView = view.findViewById(R.id.recycler_view_station_list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    public void onRefreshNow() {
//        isRefresh = false;
//        initData();
        mSmartRefreshLayout.autoRefresh();
    }

    private void initData() {
        if (!isRefresh) {
            showProgress("加载中...", false);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base = "http://api.hclyz.cn:81/mf/";
                SPUtil.putString(MyApp.getInstance(), "baseUrlFromServer", base);
                String baseCurren;
                if (SPUtil.getBoolen(MyApp.getInstance(), "defaultBase", true)) {
                    baseCurren = base;
                } else {
                    baseCurren = SPUtil.getString(MyApp.getInstance(), "customBase", base);
                }
                String jsonListData = getStringFromNet(baseCurren + "json.txt");
                Log.d("flag", "获取的json数据： \n" + jsonListData);
                Gson gson = new Gson();
                StationBean bean = gson.fromJson(jsonListData, StationBean.class);
                final List<StationListItemDataBean> data = bean.getPingtai();
                Log.d("flag", "获取的json数据的长度为： " + data.size());
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mList.clear();
                        mAdapter.removeAll();
                        mAdapter.addAll(data);
                    }
                });
                mSmartRefreshLayout.finishRefresh();
                hideProgress();

            }
        }).start();
        mSmartRefreshLayout.finishRefresh();
        hideProgress();
    }


    private String getStringFromNet(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            ResponseBody responseBody = clone.body();
            byte[] bytes = IOUtils.toByteArray(Objects.requireNonNull(responseBody).byteStream());
            MediaType contentType = responseBody.contentType();
            String body = new String(bytes, getCharset(contentType));
            Log.d("flag", "getStringFromNet方法获取的数据： \n" + body);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }


    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        isRefresh = true;
        initData();
    }

    @Override
    public void onRecyclerItemClick(RecyclerView recycler, View view, int position, long id, StationListItemDataBean data) {
        Intent intent = new Intent(getContext(), LiveRoomListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", data);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("flag", "走了LiveStationListFragment onActivityResult");
        switch (resultCode) {
            case Activity.RESULT_CANCELED:
                Log.d("flag", "走了MainActivity onActivityResult RESULT_CANCELED");
                break;
            case Activity.RESULT_OK:
                if (requestCode == CHANGE_CODE) {
                    onRefreshNow();
                }
                break;
        }
    }
}
