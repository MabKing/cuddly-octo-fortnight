package com.prpr894.cplayer.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
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
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.adapters.recycleradapters.StationRecyclerAdapter;
import com.prpr894.cplayer.base.BaseFragment;
import com.prpr894.cplayer.bean.StationBean;
import com.prpr894.cplayer.bean.StationListItemDataBean;
import com.prpr894.cplayer.ui.activities.LiveRoomListActivity;
import com.prpr894.cplayer.ui.activities.MainActivity;
import com.prpr894.cplayer.ui.activities.MainPlayerActivity;
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

    public LiveStationListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

    private void initData() {
        if (!isRefresh) {
            showProgress("加载中...", false);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://ww.jiafangmao.com/jk.txt";
                String base = getStringFromNet(url);
                if (!base.equals("")) {
//                    String jsonListData = getStringFromNet(base + "/xyjk.html");
                    String jsonListData = getStringFromNet("http://ww.jiafangmao.com/6/xyjk.html");
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
                                    hideProgress();
                                    mSmartRefreshLayout.finishRefresh();
                                }
                            });
                        }else {
                            hideProgress();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideProgress();
                    }
                }
            }
        }).start();
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
}
