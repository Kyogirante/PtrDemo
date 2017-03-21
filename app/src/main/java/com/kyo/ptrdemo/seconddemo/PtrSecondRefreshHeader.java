package com.kyo.ptrdemo.seconddemo;

import android.content.Context;
import android.util.AttributeSet;

import com.kyo.ptrdemo.view.PtrBaseRefreshHeader;

/**
 * @author KyoWang
 * @since 2017/03/10
 */

public class PtrSecondRefreshHeader extends PtrBaseRefreshHeader {

    public PtrSecondRefreshHeader(Context context) {
        super(context);
    }

    public PtrSecondRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PtrSecondRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PtrSecondRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initOther() {

    }

    @Override
    public void updateCircle(float percent) {
        mHeaderCircle.update(percent);
    }
}
