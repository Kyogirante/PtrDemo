package com.kyo.ptrdemo.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kyo.ptrdemo.R;

/**
 * @author KyoWang
 * @since 2017/03/10
 */

public abstract class PtrBaseRefreshHeader extends LinearLayout {
    protected LinearLayout mHeaderViewContent;
    protected TextView mHeaderText;
    protected PtrRefreshCircleHeader mHeaderCircle;
    protected ImageView mHeaderArrow;
    protected ProgressBar mProgressBar;

    public PtrBaseRefreshHeader(Context context) {
        super(context);
        init(context);
    }

    public PtrBaseRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PtrBaseRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PtrBaseRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        mHeaderViewContent = getContentView(context);
        mHeaderText = (TextView) mHeaderViewContent.findViewById(R.id.ptr_header_text);
        mHeaderCircle = (PtrRefreshCircleHeader) mHeaderViewContent.findViewById(R.id.ptr_header_circle);
        mHeaderArrow = (ImageView) mHeaderViewContent.findViewById(R.id.ptr_header_arrow);
        mProgressBar = (ProgressBar) mHeaderViewContent.findViewById(R.id.ptr_header_loading);

        initOther();
    }

    protected LinearLayout getContentView(Context context) {
        return (LinearLayout) LayoutInflater.from(context).inflate(R.layout.ptr_view_refresh_header, this, true);
    }

    public abstract void initOther();

    public void updateText(int resId) {
        mHeaderText.setText(resId);
    }

    public void startLoading() {
        mHeaderCircle.setVisibility(GONE);
        mHeaderArrow.setVisibility(GONE);
        mProgressBar.setVisibility(VISIBLE);
    }

    public void stopLoading() {
        mProgressBar.setVisibility(GONE);
        mHeaderCircle.setVisibility(VISIBLE);
        mHeaderArrow.setVisibility(VISIBLE);
    }

    public abstract void updateCircle(float percent);
}
