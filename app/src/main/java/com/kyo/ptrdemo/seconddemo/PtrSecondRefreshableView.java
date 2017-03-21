package com.kyo.ptrdemo.seconddemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

import com.kyo.ptrdemo.R;

/**
 * @author KyoWang
 * @since 2017/03/15
 */

public class PtrSecondRefreshableView extends LinearLayout {

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_PULL_TO_REFRESH = 2;
    public static final int STATUS_RELEASE_TO_REFRESH = 3;
    public static final int STATUS_REFRESHING = 4;

    public static final float DRAG_COEFFICIENT_NORMAL = 0.7F;
    public static final float DRAG_COEFFICIENT_LIMIT = 0.4F;

    private PtrSecondRefreshHeader mHeader;
    private ListView mListView;

    private int mStatus = STATUS_NORMAL;
    private boolean isListViewMove = false;
    private int mLayoutContentHeight = 0;
    private float mLastMoveY;
    private float mLastDownY;

    private Scroller mScroller;
    private OnRefreshListener mListener;

    public PtrSecondRefreshableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PtrSecondRefreshableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mListView = (ListView) getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 测量子View大小
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mLayoutContentHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == mHeader) { // 如果是刷新头部，向上偏移
                child.layout(0, 0 - child.getMeasuredHeight(), child.getMeasuredWidth(), 0);
            } else {
                child.layout(0, mLayoutContentHeight, child.getMeasuredWidth(), mLayoutContentHeight + child.getMeasuredHeight());
                mLayoutContentHeight += child.getMeasuredHeight();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int nowY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(mStatus == STATUS_REFRESHING) {
                    float distance = mLastDownY - nowY;
                    // 如果手势向下滑动且列表中第一个Item可见，向下移动全部子View
                    if(distance < 0
                            && mListView.getFirstVisiblePosition() == 0
                            && mListView.getChildAt(0).getTop()==0)  {
                        scrollBy(0, (int) (distance * DRAG_COEFFICIENT_LIMIT));
                        isListViewMove = true;
                        mLastDownY = nowY;

                        return true;
                    } else { // 如果手势向上滑动
                        if(getScrollY() < 0) { // 当Header没有完全隐藏，移动全部子View;当Header完全隐藏，将事件传递给ListView
                            if(getScrollY() + distance > 0) {
                                scrollBy(0, 0);
                            } else {
                                scrollBy(0, (int) distance);
                            }
                            mLastDownY = nowY;
                            isListViewMove = true;
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            default:
                // 用户抬起手，如果子View通过scrollBy移动过
                if(isListViewMove) {
                    isListViewMove = false;
                    // 如果子View向下移动，向下移动距离大于Header高度，则自动回弹，显示完整Header
                    if(getScrollY() < 0 && Math.abs(getScrollY()) > mHeader.getMeasuredHeight()) {
                        mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() + mHeader.getMeasuredHeight()), 200);
                        invalidate();
                    }
                    return true;
                }
                isListViewMove = false;
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = false;

        if(mStatus == STATUS_REFRESHING) {
            return false;
        }

        // 记录此次触摸事件的y坐标
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                intercept = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (y > mLastMoveY) { // 下滑操作
                    View child = getChildAt(1);
                    if (child instanceof AdapterView) {
                        AdapterView adapterChild = (AdapterView) child;
                        // 判断AbsListView是否已经到达内容最顶部(如果已经到达最顶部，就拦截事件，自己处理滑动)
                        if (adapterChild.getFirstVisiblePosition() == 0
                                || adapterChild.getChildAt(0).getTop() == 0) {
                            intercept = true;
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                intercept = false;
                break;
            }
        }

        mLastMoveY = y;

        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float nowY = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastMoveY = nowY;
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = mLastMoveY - nowY;
                if(distance < 0) { // 如果是向下滑动，移动子View
                    // 头部没有完全展示
                    if(Math.abs(getScrollY()) <= mHeader.getMeasuredHeight()) {
                        mStatus = STATUS_PULL_TO_REFRESH;
                        scrollBy(0, (int) (distance * DRAG_COEFFICIENT_NORMAL));
                    } else { // 头部已经完全展示
                        scrollBy(0, (int) (distance * DRAG_COEFFICIENT_LIMIT));
                        mHeader.updateText(R.string.ptr_refresh_release);
                        mStatus = STATUS_RELEASE_TO_REFRESH;
                    }
                } else { // 如果是向上滑动，移动子View
                    if(getScrollY() < 0) {
                        scrollBy(0, (int) distance);
                    }
                    if(Math.abs(getScrollY()) <= mHeader.getMeasuredHeight()) {
                        mStatus = STATUS_PULL_TO_REFRESH;
                        mHeader.updateText(R.string.ptr_refresh_normal);
                    }
                }

                // 更新刷新头部动画
                mHeader.updateCircle(Math.abs(getScrollY() * 1f/ mHeader.getMeasuredHeight()));

                break;
            case MotionEvent.ACTION_UP:
            default:
                if(mStatus == STATUS_RELEASE_TO_REFRESH) { // 用户松开手后，如果是松开刷新状态，则回弹显示完整Header，并刷新数据
                    mHeader.updateText(R.string.ptr_refresh_refreshing);
                    mHeader.startLoading();
                    mStatus = STATUS_REFRESHING;

                    if(mListener != null) {
                        mListener.onRefresh();
                    }

                    mHeader.post(new Runnable() {
                        @Override
                        public void run() {
                            mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() + mHeader.getMeasuredHeight()), 200);
                            invalidate();
                        }
                    });
                } else if(mStatus == STATUS_PULL_TO_REFRESH){ // 用户松开手后，如果是下拉刷新状态，则隐藏Header
                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 200);
                    invalidate();
                    mHeader.updateText(R.string.ptr_refresh_normal);
                    mStatus = STATUS_NORMAL;
                }
                break;
        }

        mLastMoveY = nowY;
        return true;
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    public void setOnRefreshListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }

    public void finishRefresh() {
        if(!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        scrollTo(0, 0);

        mHeader.updateText(R.string.ptr_refresh_normal);
        mHeader.stopLoading();
        mStatus = STATUS_NORMAL;
    }

    private void init(Context context) {
        mHeader = new PtrSecondRefreshHeader(context);
        addView(mHeader, 0);

        mScroller = new Scroller(getContext());
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}
