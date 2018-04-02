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
import com.google.gson.stream.JsonReader;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
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
                String url = "http://ww.jiafangmao.com/jk.txt";
                String base = getStringFromNet(url);
//                base = "http://ww.jiafangmao.com/3";
                if (!base.equals("")) {
                    if (SPUtil.getBoolen(MyApp.getInstance(), "defaultBase", true) && !base.startsWith("http://") && !base.startsWith("http://s")) {
//                        WebView webView=new WebView(getContext());
//                        webView.loadUrl(url);
//                        showBaiduSafeCheckDialog();
                        Log.d("flag", "获取base失败");
                        return;
                    }
                    SPUtil.putString(MyApp.getInstance(), "baseUrlFromServer", base);
                    String baseCurren;
                    if (SPUtil.getBoolen(MyApp.getInstance(), "defaultBase", true)) {
                        baseCurren = base;
                    } else {
                        baseCurren = SPUtil.getString(MyApp.getInstance(), "customBase", base);
                    }
                    String jsonListData = getStringFromNet(baseCurren + "/xyjk.html");
//                    String jsonListData = getStringFromNet("http://ww.jiafangmao.com/6/xyjk.html");
                    Log.d("flag", "获取的json数据： \n" + jsonListData);
                    String jsonListDataFormated;
                    if (jsonListData.contains(",]")) {
                        Log.d("flag", "走了格式化");
                        jsonListDataFormated = jsonListData.replace(",]", "]");
                    } else {
                        jsonListDataFormated = jsonListData;
                    }
                    try {
                        JSONObject object = new JSONObject(jsonListDataFormated);
                        if (object.has("data")) {
                            Gson gson = new Gson();
                            JsonReader reader = new JsonReader(new StringReader(jsonListDataFormated));
                            reader.setLenient(true);
                            StationBean bean = gson.fromJson(reader, StationBean.class);
                            final List<StationListItemDataBean> data = bean.getData();
                            Log.d("flag", "获取的json数据的长度为： " + data.size());
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mList.clear();
                                    mAdapter.removeAll();
                                    mAdapter.addAll(data);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mSmartRefreshLayout.finishRefresh();
                    hideProgress();
                } else {//base为空
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDialogNullBase();
                        }
                    });
                }
            }
        }).start();
        mSmartRefreshLayout.finishRefresh();
        hideProgress();
    }

    private void showBaiduSafeCheckDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }
        builder.setTitle("提示");
        builder.setMessage("获取接口的网页地址貌似开启了百度云加速的浏览器安全验证，本程序暂未处理此问题，请稍后再试试看...");
        final AlertDialog dialog = builder.create();
        dialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }, 3000);
    }

    private void showDialogNullBase() {
        AlertDialog.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }
        builder.setTitle("提示");
        builder.setMessage("获取服务器地址失败。是否尝试本地源服务器地址？");
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intentToChangeBaseActivity();
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    public void intentToChangeBaseActivity() {
        Intent intent = new Intent(getContext(), ChangeBaseUrlActivity.class);
        LiveStationListFragment.this.startActivityForResult(intent, CHANGE_CODE);
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
