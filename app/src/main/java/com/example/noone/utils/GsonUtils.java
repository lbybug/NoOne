package com.example.noone.utils;

import com.google.gson.Gson;

public class GsonUtils {

    private volatile static GsonUtils gsonUtils;

    private Gson gson;

    private GsonUtils() {
        if (gson == null) {
            gson = new Gson();
        }
    }

    public static GsonUtils getInstance() {
        if (gsonUtils == null) {
            gsonUtils = new GsonUtils();
            synchronized (GsonUtils.class){
                if (gsonUtils == null) {
                    gsonUtils = new GsonUtils();
                }
            }
        }
        return gsonUtils;
    }

    public Gson getGson() {
        return gson;
    }
}
