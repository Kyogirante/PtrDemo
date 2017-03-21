package com.kyo.ptrdemo.firstdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kyo.ptrdemo.R;

/**
 * @author KyoWang
 * @since 2017/03/10
 */

public class PtrFirstRefreshableView extends LinearLayout implements View.OnTouchListener {

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_PULL_TO_REFRESH = 2;
    public static final int STATUS_RELEASE_TO_REFRESH = 3;
    public static final int STATUS_REFRESHING = 4;

    private OnRefreshListener mListener;

    private PtrFirstRefreshHeader mHeader;
    private LayoutParams mHeaderLayoutParams;
    private ListView mListView;

    private int mHeaderHeight;

    private float mDownY;

    private int mStatus = STATUS_NORMAL;
    private int touchSlop;

    private int SCROLL_SPEED = -20;

    private boolean ableToPull = false;
    private boolean isLayouted = false;

    public PtrFirstRefreshableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PtrFirstRefreshableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        // 如果是第一次Layout, 做一些设置
        if (changed && !isLayouted) {
            isLayouted = true;
            // 设置刷新头部MarginTop, 隐藏刷新头部
            mHeaderHeight = mHeader.getHeight();
            mHeaderLayoutParams = (LayoutParams) mHeader.getLayoutParams();
            mHeader.setTopMargin(-mHeaderHeight);

            // 设置ListView的事件监听
            mListView = (ListView) getChildAt(1);
            mListView.setOnTouchListener(this);
        }
    }


    /**
     * 实例化刷新头部并将刷新头部添加的父布局
     */
    private void init() {
        mHeader = new PtrFirstRefreshHeader(getContext());
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        addView(mHeader, 0);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        checkTopShow(event);
        if (ableToPull) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float yMove = event.getRawY();
                    int distance = (int) (yMove - mDownY);
                    // 如果手指向上滑动，并且下拉头是完全隐藏的，不处理
                    if (distance <= 0 && mHeader.getTopMargin() <= -mHeaderHeight) {
                        return false;
                    }
                    if (distance < touchSlop) {
                        return false;
                    }
                    if (mStatus != STATUS_REFRESHING) {
                        if (mHeader.getTopMargin()  > 0) {
                            mStatus = STATUS_RELEASE_TO_REFRESH;
                        } else {
                            mStatus = STATUS_PULL_TO_REFRESH;
                        }
                        // 通过偏移下拉头的topMargin值，来实现下拉效果
                        int topMargin = (distance / 2) - mHeaderHeight;
                        mHeader.setTopMargin(topMargin);

                        // 更新刷新头部圆环动画
                        mHeader.updateCircle(Math.abs(distance * 1f / 2f / mHeaderHeight));
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (mStatus == STATUS_RELEASE_TO_REFRESH) {
                        // 松手时如果是释放立即刷新状态，就去调用正在刷新的任务
                        mStatus = STATUS_REFRESHING;
                        updateHeaderView();
                        new RefreshingTask().execute();
                        mHeader.startLoading();
                    } else if (mStatus == STATUS_PULL_TO_REFRESH) {
                        // 松手时如果是下拉状态，就去调用隐藏下拉头的任务
                        mStatus = STATUS_NORMAL;
                        updateHeaderView();
                        new HideHeaderTask().execute();
                    }
                    break;
            }

            if (mStatus == STATUS_PULL_TO_REFRESH ||
                    mStatus == STATUS_RELEASE_TO_REFRESH) {
                mListView.setPressed(false);
                mListView.setFocusable(false);
                mListView.setFocusableInTouchMode(false);
                updateHeaderView();
                return true;
            }
        }
        return false;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public void finishRefreshing() {
        mStatus = STATUS_NORMAL;
        new HideHeaderTask().execute();
        mHeader.stopLoading();
    }

    private void checkTopShow(MotionEvent event) {
        View firstChild = mListView.getChildAt(0);
        if (firstChild != null) {
            int firstVisiblePos = mListView.getFirstVisiblePosition();
            // 如果列表第一个item可见且距离ListView顶部为0，则说明ListView已经到最顶部，此时可以下拉刷新
            if (firstVisiblePos == 0 && firstChild.getTop() == 0) {
                ableToPull = true;
            } else {
                if (mHeader.getTopMargin() != -mHeaderHeight) {
                    mHeader.setTopMargin(-mHeaderHeight);
                }
                ableToPull = false;
            }
        } else {
            ableToPull = true;
        }
    }

    private void updateHeaderView() {
        if (mStatus == STATUS_PULL_TO_REFRESH) {
            pullToRefresh();
        } else if (mStatus == STATUS_RELEASE_TO_REFRESH) {
            releaseToRefresh();
        } else if(mStatus == STATUS_REFRESHING) {
            refreshing();
        }
    }

    private void pullToRefresh() {
        mHeader.updateText(R.string.ptr_refresh_normal);
    }

    private void releaseToRefresh() {
        mHeader.updateText(R.string.ptr_refresh_release);
    }

    private void refreshing() {
        mHeader.updateText(R.string.ptr_refresh_refreshing);
    }

    class RefreshingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int topMargin = mHeaderLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= 0) {
                    topMargin = 0;
                    break;
                }
                publishProgress(topMargin);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(0);
            if (mListener != null) {
                mListener.onRefresh();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            mHeader.setTopMargin(topMargin[0]);
        }

    }

    class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = mHeaderLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= -mHeaderHeight) {
                    topMargin = -mHeaderHeight;
                    break;
                }
                publishProgress(topMargin);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            mHeader.setTopMargin(topMargin[0]);
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            mHeader.setTopMargin(topMargin);
        }
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}
