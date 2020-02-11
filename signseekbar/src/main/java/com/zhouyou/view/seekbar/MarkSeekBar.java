package com.zhouyou.view.seekbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * <p>描述：一个漂亮而强大的Android自定义SeekBar，它有一个带有进度的提示牌 sign:指示牌。</p>
 * 作者： zhouyou<br>
 * 日期： 2017/10/10 15:55 <br>
 * 版本： v1.0<br>
 */
public class MarkSeekBar extends View {
    //progress最小值
    private float mMin;
    // progress最大值
    private float mMax;
    // 当前值
    private float mProgress;
    //已缓存显示
    private float mCacheProgress;
    // 进度条高度
    private int mTrackSize;
    // 已选中进度条高度
    private int mSecondTrackSize;
    //缓存条的高度size
    private int mCacheTrackSize;


    // 滑动点，半径
    private int mThumbRadius;
    // 标记点，半径
    private int mMarkRadius;
    // 拖动时的 滑动点半径
    private int mThumbRadiusOnDragging;
    // 第一轨迹颜色
    private int mTrackColor;
    // 第二轨迹颜色
    private int mSecondTrackColor;
    //缓存轨迹颜色
    private int mCacheTrackColor;
    //标记点颜色
    private int mSectionMarkColor;

    // 滑动点的颜色
    private int mThumbColor;
    // 是否显示节点标记
    private boolean isShowSectionMark;

    private ArrayList<Float> mCustomArrayFloat;

    //动画持续时间
    private long mAnimDuration;

    private boolean isTouchToSeek; // touch anywhere on track to quickly seek

    private float mDelta; // max - min
    private float mThumbCenterX; // X coordinate of thumb's center
    private float mTrackLength; // pixel length of whole track
    //每一份的宽度  ，假如（progress max是 999的话 mPerProgressWidth的值是，总宽度/999）
    private float mPerProgressWidth;

    private boolean isThumbOnDragging; // is thumb on dragging or not

    private OnProgressChangedListener mProgressListener; // progress changing listener
    //轨道的左边距离本view的间距
    private float mLeft;
    //轨道的右边距离本view的间距
    private float mRight;
    private Paint mPaint;
    private Rect mRectText;

    private float mThumbBgAlpha; //  alpha of thumb shadow
    private float mThumbRatio; // ratio of thumb shadow
    private boolean isShowThumbShadow;

    public MarkSeekBar(Context context) {
        this(context, null);
    }

    public MarkSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MarkSeekBar, defStyleAttr, 0);
        mMin = a.getFloat(R.styleable.MarkSeekBar_msb_min, 0.0f);
        mMax = a.getFloat(R.styleable.MarkSeekBar_msb_max, 100.0f);
        mProgress = a.getFloat(R.styleable.MarkSeekBar_msb_progress, mMin);
        mTrackSize = a.getDimensionPixelSize(R.styleable.MarkSeekBar_msb_track_size, SignUtils.dp2px(2));
        mSecondTrackSize = a.getDimensionPixelSize(R.styleable.MarkSeekBar_msb_second_track_size, mTrackSize + SignUtils.dp2px(2));
        mCacheTrackSize = a.getDimensionPixelSize(R.styleable.MarkSeekBar_msb_cache_track_size, mTrackSize + SignUtils.dp2px(2));
        mThumbRadius = a.getDimensionPixelSize(R.styleable.MarkSeekBar_msb_thumb_radius, mSecondTrackSize + SignUtils.dp2px(2));
        mMarkRadius = a.getDimensionPixelSize(R.styleable.MarkSeekBar_msb_mark_radius, 0);
        mThumbRadiusOnDragging = a.getDimensionPixelSize(R.styleable.MarkSeekBar_msb_thumb_radius_on_dragging, mSecondTrackSize * 2);
        mTrackColor = a.getColor(R.styleable.MarkSeekBar_msb_track_color, ContextCompat.getColor(context, R.color.colorPrimary));
        mSecondTrackColor = a.getColor(R.styleable.MarkSeekBar_msb_second_track_color, ContextCompat.getColor(context, R.color.colorAccent));
        mCacheTrackColor = a.getColor(R.styleable.MarkSeekBar_msb_cache_track_color, ContextCompat.getColor(context, R.color.colorAccent));
        mThumbColor = a.getColor(R.styleable.MarkSeekBar_msb_thumb_color, mSecondTrackColor);
        isShowSectionMark = a.getBoolean(R.styleable.MarkSeekBar_msb_show_section_mark, false);
        mSectionMarkColor = a.getColor(R.styleable.MarkSeekBar_msb_section_mark_color, Color.WHITE);
        int duration = a.getInteger(R.styleable.MarkSeekBar_msb_anim_duration, -1);
        mAnimDuration = duration < 0 ? 200 : duration;
        isTouchToSeek = a.getBoolean(R.styleable.MarkSeekBar_msb_touch_to_seek, false);

        mThumbBgAlpha = a.getFloat(R.styleable.MarkSeekBar_msb_thumb_bg_alpha, 0.2f);
        mThumbRatio = a.getFloat(R.styleable.MarkSeekBar_msb_thumb_ratio, 0.7f);
        isShowThumbShadow = a.getBoolean(R.styleable.MarkSeekBar_msb_show_thumb_shadow, false);
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mRectText = new Rect();
        initConfigByPriority();
    }


    private void initConfigByPriority() {
        if (mMin == mMax) {
            mMin = 0.0f;
            mMax = 100.0f;
        }
        if (mMin > mMax) {
            float tmp = mMax;
            mMax = mMin;
            mMin = tmp;
        }
        if (mProgress < mMin) {
            mProgress = mMin;
        }
        if (mProgress > mMax) {
            mProgress = mMax;
        }

        if(mCacheProgress<mMin){
            mCacheProgress = mMin;
        }
        if(mCacheProgress>mMax){
            mCacheProgress=mMax;
        }

        if (mSecondTrackSize < mTrackSize) {
            mSecondTrackSize = mTrackSize + SignUtils.dp2px(2);
        }
        if (mThumbRadius <= mSecondTrackSize) {
            mThumbRadius = mSecondTrackSize + SignUtils.dp2px(2);
        }
        if(mMarkRadius == 0){
            mMarkRadius = mSecondTrackSize + SignUtils.dp2px(2);
        }
        if (mThumbRadiusOnDragging <= mSecondTrackSize) {
            mThumbRadiusOnDragging = mSecondTrackSize * 2;
        }
        mDelta = mMax - mMin;

        setProgress(mProgress);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = mThumbRadiusOnDragging * 2; // 默认高度为拖动时thumb圆的直径
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);

        mLeft = getPaddingLeft() + mThumbRadiusOnDragging;
        mRight = getMeasuredWidth() - getPaddingRight() - mThumbRadiusOnDragging;

        //轨迹长度
        mTrackLength = mRight - mLeft;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float xLeft = getPaddingLeft();
        float xRight = getMeasuredWidth() - getPaddingRight();
        float yTop = getPaddingTop() + mThumbRadiusOnDragging;

        //轨迹长度/总size    得到的结果
        mPerProgressWidth = mTrackLength * 1f / mMax;

        xLeft += mThumbRadiusOnDragging;
        xRight -= mThumbRadiusOnDragging;

        if (!isThumbOnDragging) {
            mThumbCenterX = mTrackLength / mDelta * (mProgress - mMin) + xLeft;
        }

        // draw track
        mPaint.setColor(mSecondTrackColor);
        mPaint.setStrokeWidth(mSecondTrackSize);
        canvas.drawLine(xLeft, yTop, mThumbCenterX, yTop, mPaint);

        // draw second track
        mPaint.setColor(mTrackColor);
        mPaint.setStrokeWidth(mTrackSize);
        canvas.drawLine(mThumbCenterX, yTop, xRight, yTop, mPaint);

        //画缓存进度条
        if(mCacheProgress*mPerProgressWidth>mThumbCenterX){
            mPaint.setColor(mCacheTrackColor);
            mPaint.setStrokeWidth(mCacheTrackSize);
            canvas.drawLine(mThumbCenterX, yTop, xLeft+mCacheProgress*mPerProgressWidth, yTop, mPaint);
        }

        //boolean conditionInterval = mSectionCount % 2 == 0;
        boolean conditionInterval = true;

        // draw sectionMark
        if(isShowSectionMark){
            drawCustomMark(canvas, xLeft, yTop, conditionInterval);
        }

        // draw thumb
        mPaint.setColor(mThumbColor);
        //draw thumb shadow
        if (isShowThumbShadow) {
            canvas.drawCircle(mThumbCenterX, yTop, isThumbOnDragging ? mThumbRadiusOnDragging * mThumbRatio : mThumbRadius * mThumbRatio, mPaint);
            mPaint.setColor(getColorWithAlpha(mThumbColor, mThumbBgAlpha));
        }
        //Paint paint = new Paint();
        //Shader shader = new RadialGradient(mThumbCenterX, yTop, isThumbOnDragging ? mThumbRadiusOnDragging : mThumbRadius, mThumbColor, getColorWithAlpha(mThumbColor, mThumbBgAlpha), Shader.TileMode.CLAMP);
        //paint.setShader(shader);
        canvas.drawCircle(mThumbCenterX, yTop, isThumbOnDragging ? mThumbRadiusOnDragging : mThumbRadius, mPaint);
    }

    /**
     * 画自定义节点标记
     * @param canvas
     * @param xLeft
     * @param yTop
     * @param conditionInterval 条件区间，外界传过来的总是true
     */
    private void drawCustomMark(Canvas canvas, float xLeft, float yTop, boolean conditionInterval) {
        // 交汇点x轴坐标
        float junction = mTrackLength / mDelta * Math.abs(mProgress - mMin) + mLeft;
        //设置文本画笔的文字大小及边框
        mPaint.getTextBounds("0123456789", 0, "0123456789".length(), mRectText); // compute solid height

        float x_;
        for (int i = 0; i < mCustomArrayFloat.size(); i++) {
            x_ = xLeft + mCustomArrayFloat.get(i) * mPerProgressWidth;
            mPaint.setColor(x_ <= junction ? mSecondTrackColor : mSectionMarkColor);
            // sectionMark
            canvas.drawCircle(x_, yTop, mMarkRadius, mPaint);
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    float dx;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                isThumbOnDragging = isThumbTouched(event);
                //拖动中
                if (isThumbOnDragging) {
                    invalidate();
                } else if (isTouchToSeek && isTrackTouched(event)) {
                    //非拖动中允许点击到指定位置的话则
                    isThumbOnDragging = true;
                    mThumbCenterX = event.getX();
                    if (mThumbCenterX < mLeft) {
                        mThumbCenterX = mLeft;
                    }
                    if (mThumbCenterX > mRight) {
                        mThumbCenterX = mRight;
                    }
                    mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                    invalidate();
                }

                dx = mThumbCenterX - event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isThumbOnDragging) {
                    mThumbCenterX = event.getX() + dx;
                    if (mThumbCenterX < mLeft) {
                        mThumbCenterX = mLeft;
                    }
                    if (mThumbCenterX > mRight) {
                        mThumbCenterX = mRight;
                    }
                    mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                    invalidate();

                    if (mProgressListener != null) {
                        mProgressListener.onProgressChanged(this, getProgress(), true);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);

                if (isThumbOnDragging || isTouchToSeek) {
                    animate()
                            .setDuration(mAnimDuration)
                            .setStartDelay(!isThumbOnDragging && isTouchToSeek ? 300 : 0)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    isThumbOnDragging = false;
                                    invalidate();

                                    if (mProgressListener != null) {
                                        mProgressListener.onProgressChanged(MarkSeekBar.this,
                                                getProgress(), true);
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    isThumbOnDragging = false;
                                    invalidate();
                                }
                            })
                            .start();
                }

                if (mProgressListener != null) {
                    mProgressListener.getProgressOnActionUp(this, getProgress());
                }

                break;
        }

        return isThumbOnDragging || isTouchToSeek || super.onTouchEvent(event);
    }

    /**
     * 计算新的透明度颜色
     *
     * @param color 旧颜色
     * @param ratio 透明度系数
     */
    public int getColorWithAlpha(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }

    /**
     * Detect effective touch of thumb
     */
    private boolean isThumbTouched(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        float mCircleR = isThumbOnDragging ? mThumbRadiusOnDragging : mThumbRadius;
        float x = mTrackLength / mDelta * (mProgress - mMin) + mLeft;
        float y = getMeasuredHeight() / 2f;
        return (event.getX() - x) * (event.getX() - x) + (event.getY() - y) * (event.getY() - y)
                <= (mLeft + mCircleR) * (mLeft + mCircleR);
    }

    /**
     * Detect effective touch of track
     */
    private boolean isTrackTouched(MotionEvent event) {
        return isEnabled() && event.getX() >= getPaddingLeft() && event.getX() <= getMeasuredWidth() - getPaddingRight()
                && event.getY() >= getPaddingTop() && event.getY() <= getMeasuredHeight() - getPaddingBottom();
    }


    public float getMin() {
        return mMin;
    }

    public float getMax() {
        return mMax;
    }

    public void setProgress(float progress) {
        mProgress = progress;
//        if (mProgressListener != null) {
//            mProgressListener.onProgressChanged(this, getProgress(), false);
//            mProgressListener.getProgressOnFinally(this, getProgress(), false);
//        }
        postInvalidate();
    }

    public void setMax(float max) {
        this.mMax = max;
        //设置max后初始化 delta
        mDelta = mMax - mMin;
        postInvalidate();
    }

    public void setCustomArrayFloat(ArrayList<Float> customArrayFloat){
        mCustomArrayFloat = customArrayFloat;
        postInvalidate();
    }

    public void setCacheProgress(float cacheProgress) {
        mCacheProgress = cacheProgress;
        postInvalidate();
    }

    public float getProgress() {
        return mProgress;
    }

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        mProgressListener = onProgressChangedListener;
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("save_instance", super.onSaveInstanceState());
        bundle.putFloat("progress", mProgress);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mProgress = bundle.getFloat("progress");
            super.onRestoreInstanceState(bundle.getParcelable("save_instance"));
            setProgress(mProgress);
            return;
        }
        super.onRestoreInstanceState(state);
    }

    private String float2String(float value) {
        return String.valueOf(formatFloat(value));
    }

    private float formatFloat(float value) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        return bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 进度监听 onChanged, onActionUp, onFinally
     */
    public interface OnProgressChangedListener {

        //进度条在拖动中，或执行动画中改变回调
        void onProgressChanged(MarkSeekBar seekBar, float progress, boolean fromUser);

        //手指抬起后改变进度条
        void getProgressOnActionUp(MarkSeekBar seekBar,float progress);
    }
}
