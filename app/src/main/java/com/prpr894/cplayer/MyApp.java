package com.prpr894.cplayer;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.lzy.okgo.OkGo;
import com.prpr894.cplayer.greendao.gen.DaoMaster;
import com.prpr894.cplayer.greendao.gen.DaoSession;

import es.dmoral.toasty.MyToast;

public class MyApp extends Application {

    public static MyApp instance;

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MyToast.init(this, false, false);
        OkGo.getInstance().init(this);
        //setDatabase();
    }

    public static MyApp getInstance() {
        return instance;
    }


    /**
     * 设置greenDao
     */
    private void setDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this, "cplayer", null);//数据库名字
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
