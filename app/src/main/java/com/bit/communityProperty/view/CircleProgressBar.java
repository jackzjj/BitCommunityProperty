package com.bit.communityProperty.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.bit.communityProperty.R;

/**
 * Created by DELL60 on 2018/2/24.
 */

public class CircleProgressBar extends View {

    private int mCurrent = 0;//当前进度
    private Paint bgPaint;
    private Paint mPaintCurrent;
    private float mPaintWidth;//画笔宽度
    private int mPaintColor = Color.RED;//画笔颜色
    private int bgColor;//背景圆弧颜色
    private float sweepAngle;//背景圆弧扫过的角度
    private int location;//从哪个位置开始
    private float startAngle;//开始角度
    private int TIME = 10;//默认每隔10ms重绘一次
    private boolean isRun;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try {
                handler.postDelayed(this, TIME);
                mCurrent++;
                if (mCurrent == 100) {
                    mCurrent = -100;
                }
                invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取属性值
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        location = array.getInt(R.styleable.CircleProgressBar_progress_location, 2);
        mPaintWidth = array.getDimension(R.styleable.CircleProgressBar_progress_paint_width, dip2px(context, 4));//默认4dp
        mPaintColor = array.getColor(R.styleable.CircleProgressBar_progress_paint_color, mPaintColor);
        bgColor = array.getColor(R.styleable.CircleProgressBar_progress_bg_color, Color.GRAY);
        sweepAngle = array.getFloat(R.styleable.CircleProgressBar_progress_sweep_angle,360);
        array.recycle();
        initPaint();
    }


    private void initPaint() {
        //画笔->进度圆弧
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.STROKE);//只描边，不填充
        bgPaint.setColor(bgColor);
        bgPaint.setAntiAlias(true);//设置抗锯齿
        bgPaint.setStrokeWidth(mPaintWidth);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);

        mPaintCurrent = new Paint();
        mPaintCurrent.setAntiAlias(true);
        mPaintCurrent.setStrokeWidth(mPaintWidth);
        mPaintCurrent.setStyle(Paint.Style.STROKE);
        mPaintCurrent.setColor(mPaintColor);
        mPaintCurrent.setStrokeCap(Paint.Cap.ROUND);

        if (location == 1) {
            startAngle = -180;
        } else if (location == 2) {//默认从上侧开始
            startAngle = -90;
        } else if (location == 3) {
            startAngle = 0;
        } else if (location == 4) {
            startAngle = 90;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制当前进度
        RectF rectF = new RectF(mPaintWidth / 2, mPaintWidth / 2, getWidth() - mPaintWidth / 2, getHeight() - mPaintWidth / 2);
        float sweepAngle = 360 * mCurrent / 100;
        canvas.drawArc(rectF, startAngle, this.sweepAngle, false, bgPaint);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, mPaintCurrent);
    }

    /**
     * 开始
     */
    public void start() {
        if (!isRun){
            handler.postDelayed(runnable, TIME); //每隔TIMEms执行
            isRun = !isRun;
        }
    }

    /**
     * 结束
     */
    public void stop(){
        isRun = false;
        handler.removeCallbacks(runnable);
        mCurrent = 0;
        invalidate();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
