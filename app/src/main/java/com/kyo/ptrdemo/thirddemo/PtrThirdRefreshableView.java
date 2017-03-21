package com.kyo.ptrdemo.thirddemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.Scroller;

import com.kyo.ptrdemo.R;

/**
 * @author KyoWang
 * @since 2017/03/17
 */

public class PtrThirdRefreshableView extends ListView {
    private float mLastY = -1;
    private Scroller mScroller;

    private OnRefreshListener mRefreshListener;
    private PtrThirdRefreshHeader mHeader;
    private View mHeaderContainer;
    private int mHeaderViewHeight;
    private boolean mPullRefreshing = false;

    private int mScrollBack;
    private final static int SCROLL_BACK_HEADER = 0;
    private final static int SCROLL_DURATION = 200;
    private final static float OFFSET_RADIO = 0.5f;

    public PtrThirdRefreshableView(Context context) {
        super(context);
        init(context);
    }

    public PtrThirdRefreshableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PtrThirdRefreshableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PtrThirdRefreshableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());

        // 初始化Header，在初始化时候设置高度为0
        mHeader = new PtrThirdRefreshHeader(context);
        mHeaderContainer = mHeader.findViewById(R.id.dgp_header_container);

        addHeaderView(mHeader);

        mHeader.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // 获取Header的完全展示时候的高度
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        mHeaderViewHeight = mHeaderContainer.getHeight();
                        mHeader.setContentHeight(mHeaderViewHeight);
                    }
                });
    }

    public void finishRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
            mHeader.stopLoading();
        }
    }

    /**
     * 动态更新Header高度
     *
     * @param delta
     */
    private void updateHeaderHeight(float delta) {
        mHeader.setShowHeight((int) (delta + mHeader.getShowHeight()));
        if (!mPullRefreshing) {
            if (mHeader.getShowHeight() > mHeaderViewHeight) {
                mHeader.updateText(R.string.ptr_refresh_release);
            } else {
                mHeader.updateText(R.string.ptr_refresh_normal);
            }
        }
        setSelection(0);
    }

    private void resetHeaderHeight() {
        int height = mHeader.getShowHeight();
        if (height == 0)
            return;
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0;
        // 如果当前是刷新中状态，且Header的展示高度要大于Header的真实高度，则滑动列表，完整展示Header，否则隐藏Header
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLL_BACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                // 如果列表中第一个Item是可见的, 且Header的部分可见或者向下滑动，则动态设置Header高度
                if (getFirstVisiblePosition() == 0
                        && (mHeader.getShowHeight() > 0 || deltaY > 0)) {
                    updateHeaderHeight(deltaY * OFFSET_RADIO);
                    return true;
                }
                break;
            default:
                mLastY = -1;
                // 用户松开手时候，如果列表第一个Item可以见
                if (getFirstVisiblePosition() == 0) {
                    // 如果Header展示的高度大于Header的真正高度，则可刷新
                    if (mHeader.getShowHeight()  > mHeaderViewHeight) {
                        mPullRefreshing = true;
                        mHeader.updateText(R.string.ptr_refresh_refreshing);
                        mHeader.startLoading();
                        if (mRefreshListener != null) {
                            mRefreshListener.onRefresh();
                        }
                    }
                    // 根据当前情况重置Header高度
                    resetHeaderHeight();
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 使用了Scroller, 需要复写该方法
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLL_BACK_HEADER) {
                mHeader.setShowHeight(mScroller.getCurrY());
            }
            postInvalidate();
        }
        super.computeScroll();
    }

    public void setRefreshListener(OnRefreshListener mRefreshListener) {
        this.mRefreshListener = mRefreshListener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

}
