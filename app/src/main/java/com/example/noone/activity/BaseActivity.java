package com.example.noone.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xuexiang.xui.XUI;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by Lee on 2019/5/11.
 */

public abstract class BaseActivity extends SupportActivity {

    private static final String TAG = "BaseActivity";

    private static final String LAYOUT_LINEAR = "LinearLayout";
    private static final String LAYOUT_FRAME = "FrameLayout";
    private static final String LAYOUT_RELATIVE = "RelativeLayout";

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN);//解决Android软键盘出现把原来的布局给顶上去的方法
        setContentView(initLayout());
        ButterKnife.bind(this);
        initView();
        initData();
        initBroadcast();
        requestData();
    }

    @Nullable
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.equals(LAYOUT_FRAME))
        {
            view = new AutoFrameLayout(context, attrs);
        }

        if (name.equals(LAYOUT_LINEAR))
        {
            view = new AutoLinearLayout(context, attrs);
        }

        if (name.equals(LAYOUT_RELATIVE))
        {
            view = new AutoRelativeLayout(context, attrs);
        }

        if (view != null) return view;

        return super.onCreateView(name, context, attrs);
    }

    public abstract int initLayout();

    public abstract void initView();

    public abstract void initData();

    public abstract void initBroadcast();

    public abstract void requestData();

}
