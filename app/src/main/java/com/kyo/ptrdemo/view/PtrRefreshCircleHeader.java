package com.kyo.ptrdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author KyoWang
 * @since 2017/03/10
 */

public class PtrRefreshCircleHeader extends View {
    private float mWidthHeight = 0f;
    private float mCircleRadius = 0f;
    private float mCircleWidth = 5f;
    private boolean isInit = false;
    private Circle mBgCircle;
    private Circle mPercentCircle;
    private float mCurrentPercent = 1f;

    public PtrRefreshCircleHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PtrRefreshCircleHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        init();

        mBgCircle.draw(canvas, 1f);
        mPercentCircle.draw(canvas, mCurrentPercent);

        super.onDraw(canvas);
    }

    public void update(float percent) {
        mCurrentPercent = percent > 1 ? 1 : percent;
        invalidate();
    }

    private void init() {
        if (!isInit) {
            isInit = true;
            mWidthHeight = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mCircleRadius = mWidthHeight * 3f / 10f;
            mBgCircle = new Circle(mCircleRadius, mWidthHeight, mCircleWidth, Color.LTGRAY);
            mPercentCircle = new Circle(mCircleRadius, mWidthHeight, mCircleWidth, Color.GRAY);
        }
    }


    private class Circle {
        Paint paint;
        RectF rectF;


        public Circle(float radius, float widthHeight, float circleWidth, int color) {
            this.paint = new Paint();
            paint.reset();
            this.paint.setStrokeWidth(circleWidth);
            this.paint.setAntiAlias(true);
            this.paint.setColor(color);
            this.paint.setStyle(Paint.Style.STROKE);

            float leftPadding = widthHeight/2f - radius - circleWidth;
            float topPadding = leftPadding;
            float rightPadding = widthHeight - leftPadding;
            float bottomPadding = widthHeight - topPadding;
            this.rectF = new RectF( leftPadding, topPadding, rightPadding, bottomPadding);
        }

        public void draw(Canvas canvas, float percent) {
            canvas.drawArc(rectF, -90, 360 * percent, false, paint);
        }
    }
}
