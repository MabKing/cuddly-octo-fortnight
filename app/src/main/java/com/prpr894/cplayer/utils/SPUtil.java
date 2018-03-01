package com.prpr894.cplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 工具类
 */
public class SPUtil {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static void putBoolen(Context context, String key, boolean val) {
        sharedPreferences = context.getSharedPreferences(AppConfig.KEY_APP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(key, val);
        editor.apply();
    }

    public static void putInt(Context context, String key, int val) {
        sharedPreferences = context.getSharedPreferences(AppConfig.KEY_APP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    public static void putFloat(Context context, String key, float val) {
        sharedPreferences = context.getSharedPreferences(AppConfig.KEY_APP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putFloat(key, val);
        editor.apply();
    }

    public static void putLong(Context context, String key, long val) {
        sharedPreferences = context.getSharedPreferences(AppConfig.KEY_APP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putLong(key, val);
        editor.apply();
    }

    public static void putString(Context context, String key, String val) {
        sharedPreferences = context.getSharedPreferences(AppConfig.KEY_APP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, val);
        editor.apply();
    }

    public static boolean getBoolen(Context context, String key, boolean defaultVal) {
        sharedPreferences = context.getSharedPreferences(AppConfig.KEY_APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultVal);
    }

    public static int getInt(Context context, String key, int defaultVal) {
        sharedPreferences = context.getSharedPreferences(AppConfig.KEY_APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultVal);
    }

    public static float getFloat(Context context, String key, float defaultVal) {
        sharedPreferences = context.getSharedPreferences(AppConfig.KEY_APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key, defaultVal);
    }

    public static long getLong(Context context, String key, long defaultVal) {
        sharedPreferences = context.getSharedPreferences(AppConfig.KEY_APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultVal);
    }

    public static String getString(Context context, String key, String defaultVal) {
        sharedPreferences = context.getSharedPreferences(AppConfig.KEY_APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultVal);
    }

}
