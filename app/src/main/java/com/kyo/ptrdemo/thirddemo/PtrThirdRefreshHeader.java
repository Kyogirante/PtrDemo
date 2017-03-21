package com.kyo.ptrdemo.thirddemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.kyo.ptrdemo.R;
import com.kyo.ptrdemo.view.PtrBaseRefreshHeader;

/**
 * @author KyoWang
 * @since 2017/03/10
 */

public class PtrThirdRefreshHeader extends PtrBaseRefreshHeader {

    private int mContentHeight;

    public PtrThirdRefreshHeader(Context context) {
        super(context);
    }

    public PtrThirdRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PtrThirdRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PtrThirdRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected LinearLayout getContentView(Context context) {
        return (LinearLayout) LayoutInflater.from(context).inflate(R.layout.ptr_view_refresh_header_2, null, false);
    }

    @Override
    public void initOther() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        addView(mHeaderViewContent, lp);
        setGravity(Gravity.BOTTOM);
    }

    @Override
    public void updateCircle(float percent) {

    }

    public void setShowHeight(int height) {
        if (height < 0) {
            height = 0;
        }

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mHeaderViewContent
                .getLayoutParams();
        lp.height = height;
        mHeaderViewContent.setLayoutParams(lp);

        mHeaderCircle.update(Math.abs(height) * 1f/ mContentHeight);
    }

    public int getShowHeight() {
        return mHeaderViewContent.getLayoutParams().height;
    }

    public void setContentHeight(int height) {
        this.mContentHeight = height;
    }
}
