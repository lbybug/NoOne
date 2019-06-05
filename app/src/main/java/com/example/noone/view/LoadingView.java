package com.example.noone.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.noone.R;


/**
 * Created by Lee on 2019/4/12.
 */
public class LoadingView extends Dialog {

    private static final String TAG = "LoadingDialog";

    private String mMessage = "";
    private int mImageId;
    private boolean mCancelable;
    private RotateAnimation mRotateAnimation;

    private TextView tv_loading;
    private ImageView iv_loading;

    public Context context;

    private boolean isShowing = false;

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

    public LoadingView(@NonNull Context context) {
        this(context, R.style.LoadingDialog,false);
    }

    public LoadingView(@NonNull Context context, int themeResId, boolean cancelable) {
        super(context, themeResId);
        this.context = context;
        mImageId = R.drawable.ic_dialog_loading;
        mCancelable = cancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_loading);
        // 设置窗口大小
        WindowManager windowManager = getWindow().getWindowManager();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 0.3f;
        attributes.width = screenWidth/3;
        attributes.height = attributes.width;
        getWindow().setAttributes(attributes);
        setCancelable(mCancelable);

        iv_loading = findViewById(R.id.iv_loading);
        tv_loading = findViewById(R.id.tv_loading);
        tv_loading.setText(mMessage);
        iv_loading.setImageResource(mImageId);
        iv_loading.measure(0,0);
        mRotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setRepeatCount(-1);
    }

    public void setText(String msg){
        if (!TextUtils.isEmpty(msg)){
            if (tv_loading == null){
                tv_loading = findViewById(R.id.tv_loading);
            }
            tv_loading.setText(msg);
        }
    }

    public void startAnim(){
        iv_loading.startAnimation(mRotateAnimation);
    }

    public void stopAnim(){
        iv_loading.clearAnimation();
    }


    @Override
    public void dismiss() {
        isShowing = false;
        stopAnim();
        mRotateAnimation.cancel();
        super.dismiss();
    }

    @Override
    public void show() {
        isShowing = true;
        super.show();
        startAnim();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            // 屏蔽返回键
            return mCancelable;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void release(){
        mRotateAnimation.cancel();
        mRotateAnimation = null;
    }

}
