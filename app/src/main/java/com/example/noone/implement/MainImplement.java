package com.example.noone.implement;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.noone.activity.MainActivity;
import com.example.noone.listener.MainContact;
import com.example.noone.utils.PermissionUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainImplement implements MainContact.MainPresenter {

    private static final String TAG = "MainImplement";

    public static final String COCOBIT = "cocobit";

    private volatile static MainImplement mainImplement;

    public static final int REQUEST_PERMISSION = 0x02;
    public static final int REQUEST_BLUETOOTH_ENABLE = 0x03;

    public static final int CONNECTED = 0x04;
    public static final int DISCONNECT = 0x05;

    private int currentMode = DISCONNECT;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic allCharacteristic;

    private MainContact.MainView mainView;

    public void setMainView(MainContact.MainView mainView) {
        this.mainView = mainView;
    }

    private MainImplement() {

    }

    public static MainImplement getInstance() {
        if (mainImplement == null) {
            mainImplement = new MainImplement();
            synchronized (MainImplement.class) {
                if (mainImplement == null) {
                    mainImplement = new MainImplement();
                }
            }
        }
        return mainImplement;
    }

    @Override
    public void checkPermission(MainActivity activity, String permission) {
        if (!PermissionUtils.checkPermission(activity, permission)) {
            PermissionUtils.requestPermission(activity, new String[]{permission}, REQUEST_PERMISSION);
        } else {
            checkBle(activity);
        }
    }

    @Override
    public void checkBle(MainActivity activity) {
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            mainView.tip("BLE不可用");
            return;
        }
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            mainView.tip("无蓝牙可用");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE);
        } else {
            startSearch(COCOBIT);
        }
    }

    private boolean isScanning = false;
    private String field;

    @Override
    public void startSearch(String field) {
        if (bluetoothAdapter == null) {
            mainView.tip("蓝牙不可用");
            return;
        }
        if (isScanning) {
            return;
        }
        if (timer == null) {
            timer = new Timer();
        }
        this.field = field;
        isScanning = true;
        bluetoothAdapter.startLeScan(scanCallback);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mainView.showLoad("未找到cocobit");
                mainView.connectFailed();
                stopSearch();
            }
        }, 10 * 1000);
        mainView.showLoad("正在搜索cocobit");
    }

    @Override
    public void stopSearch() {
        isScanning = false;
        bluetoothAdapter.stopLeScan(scanCallback);
        cancelTimer();
    }

    private Timer timer;

    @Override
    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void connectDevice(Context context, BluetoothDevice device) {
        mainView.showLoad("正在连接cocobit");
        bluetoothGatt = device.connectGatt(context, false, callback);
    }

    @Override
    public void writeData(byte[] bytes) {
        if (bluetoothGatt != null && bytes.length <= 20) {
            allCharacteristic.setValue(bytes);
            bluetoothGatt.writeCharacteristic(allCharacteristic);
        }
    }

    @Override
    public void release() {
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        if (allCharacteristic != null) {
            allCharacteristic = null;
        }
        if (bluetoothAdapter != null) {
            bluetoothAdapter.disable();
            bluetoothAdapter = null;
        }
        currentMode = DISCONNECT;
    }

    public BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String deviceName = device.getName();
            if (!TextUtils.isEmpty(deviceName)) {
                if (field.equals(deviceName)) {
                    mainView.findDevice(device);
                    stopSearch();
                }
            }
        }
    };

    public BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "---------------------------->已经连接，搜索服务");
                mainView.showLoad("正在获取服务");
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "---------------------------->连接断开");
                currentMode = DISCONNECT;
                mainView.connectFailed();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                Log.d(TAG, "---------------------------->正在连接");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "---------------------------->发现服务");
                List<BluetoothGattService> services = gatt.getServices();
                boolean isAll = false;
                for (int i = 0; i < services.size(); i++) {
                    BluetoothGattService service = services.get(i);
                    List<BluetoothGattCharacteristic> bluetoothGattCharacteristics = service.getCharacteristics();
                    for (int j = 0; j < bluetoothGattCharacteristics.size(); j++) {
                        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattCharacteristics.get(j);
                        int property = bluetoothGattCharacteristic.getProperties();
                        if (!isAll) {
                            if (property == BluetoothGattCharacteristic.PROPERTY_WRITE
                                    && property == BluetoothGattCharacteristic.PROPERTY_READ
                                    && property == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
                                allCharacteristic = bluetoothGattCharacteristic;
                                isAll = true;
                                break;
                            }
                        }
                    }
                }
                List<BluetoothGattDescriptor> descriptors = allCharacteristic.getDescriptors();
                for (int j = 0; j < descriptors.size(); j++) {
                    BluetoothGattDescriptor descriptor = descriptors.get(j);
                    if (descriptor.getUuid() != null) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        if (bluetoothGatt.writeDescriptor(descriptor)) {
                            bluetoothGatt.setCharacteristicNotification(allCharacteristic, true);
                            mainView.showLoad("连接成功");
                            mainView.connectSuccess();
                            currentMode = CONNECTED;
                            break;
                        }
                    }
                }
            } else {
                mainView.showLoad("连接失败");
                mainView.connectFailed();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            mainView.tip("接收信息:" + characteristic.getValue());
            //接受跳跳版传回的信息
        }
    };

    public int getCurrentMode() {
        return currentMode;
    }
}
