package com.example.noone.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.example.noone.R;
import com.example.noone.implement.MainImplement;
import com.example.noone.listener.MainContact;
import com.example.noone.model.AsrFinishJsonData;
import com.example.noone.model.AsrPartialJsonData;
import com.example.noone.utils.GsonUtils;
import com.example.noone.utils.PermissionUtils;
import com.example.noone.view.LoadingView;
import com.google.gson.Gson;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.noone.implement.MainImplement.COCOBIT;

public class MainActivity extends BaseActivity implements MainContact.MainView {

    private static final String TAG = "MainActivity";

    private boolean isHavePermission = false;
    public String final_result = ""; //语音最后的输出结果

    private EventManager eventManager;                  //语音管理对象
    private LoadingView loadingView;                    //等待框
    private MainImplement mainImplement;                //蓝牙操作实现类
    private MainHandler mainHandler;                    //handler

    @BindView(R.id.goFly)
    RoundButton goFly;
    @BindView(R.id.goDown)
    RoundButton goDown;
    @BindView(R.id.trackOne)
    RoundButton trackOne;
    @BindView(R.id.trackTwo)
    RoundButton trackTwo;
    @BindView(R.id.connectBle)
    RoundButton connectBle;
    @BindView(R.id.disconnectBle)
    RoundButton disconnectBle;
    @BindView(R.id.clickToSay)
    RoundButton clickToSay;

    public static final int REQUEST_VOICE_CODE = 0x01;

    public static final int SHOW_LOAD = 0x02;
    public static final int DISMISS_LOAD = 0x03;
    public static final int TIP = 0x04;

    static class MainHandler extends Handler {
        WeakReference<MainActivity> weakReference;

        private MainHandler(MainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = weakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case SHOW_LOAD:
                        if (activity.loadingView == null) {
                            activity.loadingView = new LoadingView(activity);
                        }
                        if (!activity.loadingView.isShowing()) {
                            activity.loadingView.show();
                        }
                        activity.loadingView.setText((String) msg.obj);
                        break;
                    case DISMISS_LOAD:
                        if (activity.loadingView != null) {
                            activity.loadingView.dismiss();
                            activity.loadingView = null;
                        }
                        break;
                    case TIP:
                        Toast.makeText(activity, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        }
    }

    @Override
    public int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        if (mainImplement == null) {
            mainImplement = MainImplement.getInstance();
            mainImplement.setMainView(this);
        }
        if (mainHandler == null) {
            mainHandler = new MainHandler(this);
        }
    }

    public void requestPermission() {
        String[] permissions = PermissionUtils.checkPermissions(this, PermissionUtils.voicePermissions);
        if (permissions.length != 0) {
            PermissionUtils.requestPermission(this, permissions, REQUEST_VOICE_CODE);
        } else {
            isHavePermission = true;
        }
    }


    @Override
    public void initBroadcast() {

    }

    @Override
    public void requestData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance(this).release();

        if (eventManager != null) {
            eventManager.unregisterListener(voiceEvent);
            eventManager.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
            eventManager = null;
        }
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
            mainHandler = null;
        }
    }

    @OnClick({R.id.goFly, R.id.goDown, R.id.trackOne, R.id.trackTwo, R.id.connectBle, R.id.disconnectBle, R.id.clickToSay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.goFly:
                sendData(new byte[]{1});
                break;
            case R.id.goDown:
                sendData(new byte[]{4});
                break;
            case R.id.trackOne:
                sendData(new byte[]{2});
                break;
            case R.id.trackTwo:
                sendData(new byte[]{3});
                break;
            case R.id.connectBle:
                mainImplement.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case R.id.disconnectBle:
                if (mainImplement != null) {
                    mainImplement.release();
                }
                break;
            case R.id.clickToSay:
                if (isHavePermission) {
                    openVoiceFunction();
                } else {
                    requestPermission();
                }
                break;
        }
    }

    private void openVoiceFunction() {
        if (eventManager == null) {
            eventManager = EventManagerFactory.create(this, "asr");
            eventManager.registerListener(voiceEvent);
        }
        showLoad("开始识别");
        eventManager.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        Map<String, Object> params = new LinkedHashMap<>();
        String event = null;
        event = SpeechConstant.ASR_START;
        params.put(SpeechConstant.PID, 1536); // 默认1536
        params.put(SpeechConstant.DECODER, 0); // 纯在线(默认)
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN); // 语音活动检测
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 2000); // 不开启长语音。开启VAD尾点检测，即静音判断的毫秒数。建议设置800ms-3000ms
        params.put(SpeechConstant.ACCEPT_AUDIO_DATA, false);// 是否需要语音音频数据回调
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);// 是否需要语音音量数据回调

        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        eventManager.send(event, json, null, 0, 0);
    }


    public EventListener voiceEvent = new EventListener() {
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                // 引擎准备就绪，可以开始说话
                loadingView.setText("快说话");
            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
                // 检测到用户的已经开始说话
                loadingView.setText("正在检测");
            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                // 临时识别结果, 长语音模式需要从此消息中取出结果
                parseAsrPartialJsonData(params);
            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                // 识别结束， 最终识别结果或可能的错误
                eventManager.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
                parseAsrFinishJsonData(params);
            }
        }
    };

    private void parseAsrPartialJsonData(String data) {
        Log.d(TAG, "parseAsrPartialJsonData data:" + data);
        Gson gson = GsonUtils.getInstance().getGson();
        AsrPartialJsonData jsonData = gson.fromJson(data, AsrPartialJsonData.class);
        String resultType = jsonData.getResult_type();
        Log.d(TAG, "resultType:" + resultType);
        if (resultType != null && resultType.equals("final_result")) {
            final_result = jsonData.getBest_result();
        }
    } //识别中

    private void parseAsrFinishJsonData(String data) {
        Log.d(TAG, "parseAsrFinishJsonData data:" + data);
        Gson gson = GsonUtils.getInstance().getGson();
        AsrFinishJsonData jsonData = gson.fromJson(data, AsrFinishJsonData.class);
        String desc = jsonData.getDesc();
        dismissLoad();
        if (desc != null && desc.equals("Speech Recognize success.")) {
            //识别成功
            loadingView.setText("识别成功");
            decodeResult(final_result);
        } else {
            loadingView.setText("识别失败");
            String errorCode = "\n错误码:" + jsonData.getError();
            String errorSubCode = "\n错误子码:" + jsonData.getSub_error();
            String errorResult = errorCode + errorSubCode;
            Log.d(TAG, "parseAsrFinishJsonData: 解析错误,原因是:" + desc + "\n" + errorResult);
        }
        eventManager.unregisterListener(voiceEvent);
        eventManager.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        eventManager = null;
    } //识别结果

    private void decodeResult(String final_result) {
        if (final_result.contains("起飞") || final_result.contains("无人机起飞")) {
            tip(final_result);
            sendData(new byte[]{1});
        } else if (final_result.contains("降落") || final_result.contains("无人机降落")) {
            tip(final_result);
            sendData(new byte[]{4});
        } else if (final_result.contains("追踪") && final_result.contains("零号车")) {  //零号车不好识别，最好就一号车开始，我说了N遍都识别不了
            tip(final_result);
            sendData(new byte[]{2});
        } else if (final_result.contains("追踪") && final_result.contains("一号车")) {
            tip(final_result);
            sendData(new byte[]{3});
        } else if (final_result.contains("连接") && final_result.contains("蓝牙")) {
            if (mainImplement.getCurrentMode() != MainImplement.CONNECTED) {
                mainImplement.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        } else if (final_result.contains("断开") && (final_result.contains("蓝牙") || final_result.contains("连接") || final_result.contains("链接"))) {
            if (mainImplement != null) {
                mainImplement.release();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_VOICE_CODE:
                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != 0) {
                        flag = false;
                        break;
                    }
                }
                if (!flag) {
                    Log.d(TAG, "onRequestPermissionsResult: 权限未打开");
                    tip("请打开权限");
                } else {
                    openVoiceFunction();
                    isHavePermission = true;
                }
                break;
            case MainImplement.REQUEST_PERMISSION:
                if (grantResults != null && grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mainImplement.checkBle(this);
                    } else {
                        tip("请打开权限");
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainImplement.REQUEST_BLUETOOTH_ENABLE:
                if (resultCode == RESULT_OK) {
                    mainImplement.startSearch(COCOBIT);
                } else {
                    tip("请打开蓝牙");
                }
                break;
        }
    }

    @Override
    public void showLoad(String msg) {
        mainHandler.obtainMessage(SHOW_LOAD, msg).sendToTarget();
    }

    @Override
    public void dismissLoad() {
        mainHandler.obtainMessage(DISMISS_LOAD).sendToTarget();
    }

    @Override
    public void tip(String msg) {
        mainHandler.obtainMessage(TIP, msg).sendToTarget();
    }

    @Override
    public void findDevice(BluetoothDevice device) {
        if (mainImplement != null) {
            mainImplement.connectDevice(this, device);
        }
    }

    @Override
    public void connectSuccess() {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissLoad();
            }
        }, 1000);
    }

    @Override
    public void connectFailed() {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissLoad();
            }
        }, 1000);
    }

    @Override
    public void sendData(byte[] esc) {
        if (mainImplement.getCurrentMode() == MainImplement.CONNECTED) {
            mainImplement.writeData(esc);
        } else {
            tip("请先连接cocobit");
        }
    }
}
