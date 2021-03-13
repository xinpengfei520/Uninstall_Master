package com.xpf.android.uninstall.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by xpf on 2016/9/24 11:50
 * Function：自定义字母索引
 */
public class QuickIndexView extends View {

    private static final String TAG = "QuickIndexView";
    /**
     * 每条的高和宽
     */
    private int itemWidth;
    private int itemHeight;

    private final Paint mPaint;

    private final String[] mLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    public QuickIndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 把画笔初始化了
        mPaint = new Paint();
        // 设置抗锯齿
        mPaint.setAntiAlias(true);
        // 设置粗体字
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);//设置粗体
        // 设置白色
        mPaint.setColor(Color.GRAY);
        mPaint.setTextSize(24);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure():");
        // 得到每条的宽和高
        itemWidth = getMeasuredWidth();
        itemHeight = getMeasuredHeight() / mLetters.length;
    }

    private float startY;
    private int touchIndex = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                //得到按着那条
                int index = (int) (event.getY() / itemHeight);

                if (touchIndex != index) {
                    touchIndex = index;
                    //调用接口对应的方法
                    if (listener != null && touchIndex < mLetters.length) {
                        listener.onTextChange(mLetters[touchIndex]);
                    }
                    invalidate();
                    //回调导致onDraw();
                }
                break;
            case MotionEvent.ACTION_UP:
                touchIndex = -1;
                invalidate();
                break;
        }

        return true;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw():");
        for (int i = 0; i < mLetters.length; i++) {
            // 设置画笔颜色
            if (touchIndex == i) {
                mPaint.setColor(Color.GREEN);
            } else {
                mPaint.setColor(Color.GRAY);
            }

            // 得到字母
            String letter = mLetters[i];

            Rect rect = new Rect();
            mPaint.getTextBounds(letter, 0, 1, rect);//Y

            // 字母的宽和高
            int letterWidth = rect.width();
            int letterHeight = rect.height();

            float wordX = itemWidth / 2 - letterWidth / 2;
            float wordY = itemHeight / 2 + letterHeight / 2 + i * itemHeight;

            // 绘制每个字母
            canvas.drawText(letter, wordX, wordY, mPaint);
        }
    }

    /**
     * 文本变化的监听器
     */
    public interface OnTextChangeListener {
        /**
         * 当滑动文字变化的时候回调
         *
         * @param letter 被按下的字母
         */
        void onTextChange(String letter);
    }

    private OnTextChangeListener listener;

    /**
     * 设置监听文本变化
     *
     * @param listener
     */
    public void setOnTextChangeListener(OnTextChangeListener listener) {
        this.listener = listener;
    }
}
