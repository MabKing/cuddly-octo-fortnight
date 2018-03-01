package com.baidu.cloud.videoplayer.demo.info;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsStore {

    private static final String SETTINGS_SP_NAME = "video-settings";

    public static boolean isDefaultPortrait(Context context) {
        SharedPreferences spList = context.getSharedPreferences(SETTINGS_SP_NAME, 0);
        return spList.getBoolean("isPortrait", true);
    }

    public static void setPlayerFitMode(Context context, boolean isCrapping) {
        SharedPreferences spList = context.getSharedPreferences(SETTINGS_SP_NAME, 0);
        SharedPreferences.Editor editor = spList.edit();
        editor.putBoolean("isCrapping", isCrapping);
        editor.apply();
    }

    public static boolean isPlayerFitModeCrapping(Context context) {
        SharedPreferences spList = context.getSharedPreferences(SETTINGS_SP_NAME, 0);
        return spList.getBoolean("isCrapping", false);
    }
}
