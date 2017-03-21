package com.kyo.ptrdemo.firstdemo;

import android.content.Context;
import android.util.AttributeSet;

import com.kyo.ptrdemo.view.PtrBaseRefreshHeader;

/**
 * @author KyoWang
 * @since 2017/03/10
 */

public class PtrFirstRefreshHeader extends PtrBaseRefreshHeader {

    public PtrFirstRefreshHeader(Context context) {
        super(context);
    }

    public PtrFirstRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PtrFirstRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PtrFirstRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initOther() {

    }

    @Override
    public void updateCircle(float percent) {
        mHeaderCircle.update(percent);
    }

    public int getTopMargin() {
        return ((LayoutParams)getLayoutParams()).topMargin;
    }

    public LayoutParams getLinearLayoutParams() {
        return (LayoutParams) getLayoutParams();
    }
    public void setTopMargin(int margin) {
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.topMargin = margin;
        setLayoutParams(layoutParams);
    }
}
