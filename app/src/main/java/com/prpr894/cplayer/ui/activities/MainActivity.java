package com.prpr894.cplayer.ui.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.utils.IOUtils;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.adapters.recycleradapters.StationRecyclerAdapter;
import com.prpr894.cplayer.base.BaseActivity;
import com.prpr894.cplayer.bean.StationBean;
import com.prpr894.cplayer.bean.StationListItemDataBean;

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

public class MainActivity extends BaseActivity {

    private List<StationListItemDataBean> mList;

    private RecyclerView mRecyclerView;
    private StationRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        getToolbarTitle().setText("直播");

        mList = new ArrayList<>();
        mAdapter = new StationRecyclerAdapter(mList, this);
        mRecyclerView = findViewById(R.id.recycler_view_station_list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://ww.jiafangmao.com/jk.txt";
                String base = getStringFromNet(url);
                String jsonListData = getStringFromNet(base + "/xyjk.html");
                Log.d("flag", "获取的json数据： \n" + jsonListData);
                String jsonListDataFormated;
                if (jsonListData.contains(",]")) {
                    Log.d("flag", "走了格式化");
                    jsonListDataFormated = jsonListData.replace(",]", "]");
                } else {
                    jsonListDataFormated = jsonListData;
                }
                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new StringReader(jsonListDataFormated));
                reader.setLenient(true);
                StationBean bean = gson.fromJson(reader, StationBean.class);
                final List<StationListItemDataBean> data = bean.getData();
                Log.d("flag", "获取的json数据的长度为： " + data.size());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addAll(data);
                    }
                });
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
    protected boolean isShowBacking() {
        return false;
    }
}
