package com.example.noone.listener;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.example.noone.activity.MainActivity;

public interface MainContact {

    interface MainPresenter {

        void checkPermission(MainActivity activity, String permission);

        void checkBle(MainActivity activity);

        void startSearch(String field);

        void stopSearch();

        void cancelTimer();

        void connectDevice(Context context, BluetoothDevice device);

        void writeData(byte[] bytes);

        void release();
    }

    interface MainView {

        void showLoad(String msg);

        void dismissLoad();

        void tip(String msg);

        void findDevice(BluetoothDevice device);

        void connectSuccess();

        void connectFailed();

        void sendData(byte[] esc);
    }

}
