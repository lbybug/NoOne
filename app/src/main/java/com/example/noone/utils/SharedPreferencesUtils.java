package com.example.noone.utils;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    private volatile static SharedPreferencesUtils sharedPreferencesUtils;

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;

    public static final String SHARED_NAME = "wxzn";

    public static final String USER_ACCOUNT = "account";
    public static final String USER_PASSWORD = "password";
    public static final String USER_NAME = "nickName";
    public static final String USER_TOKEN = "token";
    public static final String CURRENT_GATEWAY = "currentGateway";

    public static final String DELETE_GATEWAY = "isDeleteGateway";
    public static final String IS_UPDATE_GATEWAY = "isUpdateGateway";

    public static final String DEFAULT_INSTALL = "default_install";

    public SharedPreferencesUtils() {
        sharedPreferences = AppUtils.getContext().getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPreferencesUtils getInstance() {
        if (sharedPreferencesUtils == null) {
            sharedPreferencesUtils = new SharedPreferencesUtils();
            synchronized (SharedPreferencesUtils.class) {
                if (sharedPreferencesUtils == null) {
                    sharedPreferencesUtils = new SharedPreferencesUtils();
                }
            }
        }
        return sharedPreferencesUtils;
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public float getFloat(String key) {
        return sharedPreferences.getFloat(key, 0);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public void clear(){
        sharedPreferences.edit().clear().commit();
    }

    public void remove(String key){
        sharedPreferences.edit().remove(key).commit();
    }

    public void release(){
        if (editor != null) {
            editor = null;
        }
        if (sharedPreferences != null) {
            sharedPreferences = null;
        }
        if (sharedPreferencesUtils != null) {
            sharedPreferencesUtils = null;
        }
    }

}
