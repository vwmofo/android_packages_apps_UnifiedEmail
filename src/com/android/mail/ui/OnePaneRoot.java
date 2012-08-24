package com.android.mail.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.android.mail.utils.LogTag;
import com.android.mail.utils.LogUtils;
import com.android.mail.utils.Utils;

/**
 * TODO: Insert description here. (generated by ath)
 */
public class OnePaneRoot extends FrameLayout {

    private static final String LOG_TAG = LogTag.getLogTag();

    public OnePaneRoot(Context c) {
        this(c, null);
    }

    public OnePaneRoot(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LogUtils.w(Utils.VIEW_DEBUGGING_TAG, "OnePaneLayout(%s).onMeasure() called", this);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        LogUtils.w(Utils.VIEW_DEBUGGING_TAG, "OnePaneLayout(%s).onLayout() START", this);
        super.onLayout(changed, left, top, right, bottom);
        LogUtils.w(Utils.VIEW_DEBUGGING_TAG, "OnePaneLayout(%s).onLayout() FINISH", this);
    }

    @Override
    public void requestLayout() {
        Utils.checkRequestLayout(this);
        super.requestLayout();
    }

}