package com.example.noone.utils;

import android.util.Log;

public class LoggerUtils {

    public static final int VERSION = 0x07;

    public static int currentVersion = 0x06 ;


    public static void v(String tag,String content){
        if (currentVersion < VERSION){
            Log.v(tag,content);
        }
    }

    public static void d(String tag,String content){
        if (currentVersion < VERSION){
            Log.d(tag,content);
        }
    }

    public static void i(String tag,String content){
        if (currentVersion < VERSION){
            Log.i(tag, content);
        }
    }

}
