package com.example.noone.utils;

import android.app.Application;
import android.content.Context;

import com.xuexiang.xui.XUI;

/**
 * Created by Lee on 2019/5/11.
 */

public class AppUtils extends Application{

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        XUI.init(this);
        XUI.debug(true);
    }

}
