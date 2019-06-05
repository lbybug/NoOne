package com.example.noone.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by Lee on 2018/7/2.
 */

public class PermissionUtils {

    public static String voicePermissions[] = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
//            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static boolean checkPermission(Context context, String permission) {
        int check = ContextCompat.checkSelfPermission(context, permission);
        if (check == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static String[] checkPermissions(Activity activity, String[] permissions){
        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, perm)) {
                toApplyList.add(perm);
            }
        }

        String tmpList[] = new String[toApplyList.size()];

        return toApplyList.toArray(tmpList);
    }

    public static void requestPermission(Activity activity, String[] permissions, int requestCode) {

        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, perm)) {
                toApplyList.add(perm);
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, toApplyList.toArray(tmpList), requestCode);
        }

    }

}
