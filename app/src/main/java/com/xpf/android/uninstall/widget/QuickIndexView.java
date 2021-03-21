package com.xpf.android.uninstall.widget;

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

import com.xpf.android.uninstall.utils.DensityUtils;

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

    private float startY;
    private int touchIndex = -1;

    private final Paint mPaint;
    private final Paint mLetterBgPaint;
    private final Rect mLetterRect;

    private final String[] mLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    public QuickIndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLetterRect = new Rect();
        // 把画笔初始化了
        mPaint = new Paint();
        // 设置抗锯齿
        mPaint.setAntiAlias(true);
        // 设置粗体字
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        // 设置白色
        mPaint.setColor(Color.GRAY);
        mPaint.setTextSize(24);

        mLetterBgPaint = new Paint();
        mLetterBgPaint.setAntiAlias(true);
        mLetterBgPaint.setColor(0xFF02AF6D);
    }

    /**
     * 为什么会调用 4 次？
     * onMeasure() -> itemWidth:32,itemHeight:28
     * onMeasure() -> itemWidth:32,itemHeight:22
     * onMeasure() -> itemWidth:32,itemHeight:28
     * onMeasure() -> itemWidth:32,itemHeight:22
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 得到每条的宽和高，获取的就是 xml 中设置的宽高
        itemWidth = getMeasuredWidth();
        itemHeight = getMeasuredHeight() / mLetters.length;
        Log.d(TAG, "onMeasure() -> itemWidth:" + DensityUtils.px2dp(getContext(), itemWidth) + ",itemHeight:" + DensityUtils.px2dp(getContext(), itemHeight));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // 得到按下去的字母的下标
                int index = (int) (event.getY() / itemHeight);

                if (touchIndex != index) {
                    touchIndex = index;
                    // 调用接口对应的方法
                    if (listener != null && touchIndex < mLetters.length) {
                        listener.onTextChange(mLetters[touchIndex]);
                    }

                    // 回调导致 onDraw();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                touchIndex = -1;
                invalidate();
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw():");
        for (int i = 0; i < mLetters.length; i++) {
            String letter = mLetters[i];
            mPaint.getTextBounds(letter, 0, 1, mLetterRect);

            // 字母的宽和高(每个字母的宽高都是不一样的)
            int letterWidth = mLetterRect.width();
            int letterHeight = mLetterRect.height();
            Log.d(TAG, "onDraw() -> letterWidth:" + letterWidth + ",letterHeight:" + letterHeight);

            float wordX = (float) (itemWidth / 2 - letterWidth / 2);
            float wordY = (float) (itemHeight / 2 + letterHeight / 2 + i * itemHeight);

            if (touchIndex == i) {
                // 计算圆形背景的坐标，应该是每个字母的最中心的位置，横坐标始终不变，纵坐标随着下标变化
                float circleX = (float) itemWidth / 2;
                float circleY = (float) itemHeight / 2 + i * itemHeight;

                // 先绘制按下去字母的圆形背景，再绘制字母，这样字母就在背景上面了
                canvas.drawCircle(circleX, circleY, 20, mLetterBgPaint);
                mPaint.setColor(Color.WHITE);
            } else {
                mPaint.setColor(Color.GRAY);
            }

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
