package com.prpr894.cplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static SimpleDateFormat formatterStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static SimpleDateFormat formatterFileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);

    public static String getStrDate() {
        return getStrDate(new Date());
    }

    public static String getStrDate(Date date) {
        return formatterStr.format(date);
    }

    public static String getStrFileNameDate() {
        return getStrFileNameDate(new Date());
    }

    public static String getStrFileNameDate(Date date) {
        return formatterFileName.format(date);
    }

}
