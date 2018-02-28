package com.prpr894.cplayer;

import android.app.Application;

import com.lzy.okgo.OkGo;

import es.dmoral.toasty.MyToast;

public class MyApp extends Application {

    public static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MyToast.init(this, false, false);
        OkGo.getInstance().init(this);
    }

    public static MyApp getInstance() {
        return instance;
    }
}
