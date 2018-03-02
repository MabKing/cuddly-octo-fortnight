package com.prpr894.cplayer.utils;

import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ChenShuo on 2017/6/23.
 */

public class JsonStringUtil {
    public static String getJsonString(Context context, String jsName){
        String jsonStr = null;
        try {
//            InputStream is = context.getAssets().open(jsName);
            InputStream is = new FileInputStream(jsName);
            byte[] buffer = new byte[1024];
            int length;
            StringBuilder sb = new StringBuilder();
            while((length = is.read(buffer))!= -1){
                sb.append(new String(buffer,0,length));
            }
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            Toast.makeText(context, "文件读取失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return "";
    }
}
