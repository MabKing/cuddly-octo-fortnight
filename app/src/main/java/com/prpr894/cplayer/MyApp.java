package com.prpr894.cplayer;

import android.app.Application;

import com.lzy.okgo.OkGo;

public class MyApp extends Application {

    public static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        OkGo.getInstance().init(this);
    }

    public static MyApp getInstance() {
        return instance;
    }
}
