package com.tik.caculatordemo.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tik.caculatordemo.R;


/**
 * @auth tik
 * @date 17/1/12 下午3:09
 */

public class PieChartView extends View {

    private int mWidth = 150;
    private int mHeight = 150;
    private Paint mPaint1;
    private Paint mPaint2;
    private int mAnglePercent1;
    private int mAnglePercent2;
    private int mProgress1;
    private int mProgress2;
    private int mType;
    private int mSpeed = 6;
    private boolean isStart = false;
    private OnAnimationListener onAnimCompleteListener;

    private RorateRunnable mRorateRunnable = new RorateRunnable();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint1 = new Paint();
        mPaint1.setAntiAlias(true);
        mPaint1.setColor(getResources().getColor(R.color.uc_lixi));

        mPaint2= new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setColor(getResources().getColor(R.color.uc_benjin));
    }

    public OnAnimationListener getOnAnimCompleteListener() {
        return onAnimCompleteListener;
    }

    public void setOnAnimCompleteListener(OnAnimationListener onAnimCompleteListener) {
        this.onAnimCompleteListener = onAnimCompleteListener;
    }

    public void startAnimation(int percent1, int percent2){
        mHandler.removeCallbacks(mRorateRunnable);
        mAnglePercent1 = 360 * percent1 / 100;
        mAnglePercent2 = 360 - mAnglePercent1;
        mProgress1 = 0;
        mProgress2 = 0;
        mType = 1;
        isStart = true;
        mHandler.post(mRorateRunnable);
    }

    public void clearCavas(){
        mType = 0;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("tag","---------mtype="+mType);
        if(mType == 1 || mType == 2){
            canvas.drawArc(new RectF(0, 0, mWidth, mHeight), -90, mProgress1, true, mPaint1);
        }
        if(mType == 2){
            canvas.drawArc(new RectF(0, 0, mWidth, mHeight), mAnglePercent1 - 90, mProgress2, true, mPaint2);
        }

    }

    class RorateRunnable implements Runnable{

        @Override
        public void run() {
            if(mProgress1 < mAnglePercent1){
                if(isStart && onAnimCompleteListener != null){
                    onAnimCompleteListener.onStart();
                }
                isStart = false;
                mType = 1;
                mProgress1 += mSpeed;
                if(mProgress1 >= mAnglePercent1){
                    mProgress1 = mAnglePercent1;
                }
                invalidate();
                mHandler.postDelayed(mRorateRunnable, 18);
            }else if(mProgress2 < mAnglePercent2){
                mType = 2;
                mProgress2 += mSpeed;
                if(mProgress2 > mAnglePercent2){
                    mProgress2 = mAnglePercent2;
                }
                invalidate();
                mHandler.postDelayed(mRorateRunnable, 18);
            }else{
                if(onAnimCompleteListener != null){
                    onAnimCompleteListener.onFinish();
                }
                mType = 0;
                mHandler.removeCallbacks(mRorateRunnable);
            }
        }
    }

    public interface OnAnimationListener {

        void onStart();
        void onFinish();
    }
}
