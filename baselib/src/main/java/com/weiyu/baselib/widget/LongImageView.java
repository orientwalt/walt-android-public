package com.weiyu.baselib.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.weiyu.baselib.util.BLog;

import java.io.IOException;
import java.io.InputStream;

/**
 * 加载长图的ImageView
 * 1.支持滑动的长图加载
 * 2.支持fling
 * 3.#支持单指拖动，双击放大，手势放大，拖动旋转等
 * 思路
 * 1.只加载与屏幕内的图像信息并显示，所以，当图片的宽高均超过屏幕的宽高的时候，就会涉及到图片的缩放
 * 2.将当前需要显示的图片的位置信息存储与Rect中，然后在onDraw方法中，将图片信息画至对应的区域
 * 3.监听手势及触摸事件处理滑动及fling事件
 */
public class LongImageView extends View implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private final Context mContext;
    private int mImageWidth;
    private int mImageHeight;
    private Scroller mScroller;
    private GestureDetector mGestureDetector;
    private BitmapRegionDecoder mBitDecoder;
    private BitmapFactory.Options mOptions;
    private Rect mRect;
    private int mViewWidth;
    private int mViewHeight;
    private float mScale;
    private Bitmap mBitmap;

    public LongImageView(Context context) {
        this(context, null);
    }

    public LongImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LongImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mScroller = new Scroller(mContext);
        //手势监听
        mGestureDetector = new GestureDetector(mContext, this);
        mOptions = new BitmapFactory.Options();
        //保存需要绘制的区域
        mRect = new Rect();
        setOnTouchListener(this);
    }

    /**
     * 第二步，先打到与图片处理相关的配置信息
     */

    public void setImg(InputStream inputStream) {
        if (inputStream == null) {
            return;
        }
        //1.先拿到图片对应的宽高
        //只获取图片的尺寸信息
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, mOptions);
        //拿到图片的宽高
        mImageWidth = mOptions.outWidth;
        mImageHeight = mOptions.outHeight;
        //开启复用
        mOptions.inMutable = true;
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        //区域解码器
        try {
            mBitDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            Log.e("test", "decoder-exception-->" + e.getLocalizedMessage());
        }
        mOptions.inJustDecodeBounds = false;
    }

    /**
     * 第三步，拿到当前view的尺寸，并计算缩放系数
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        mRect.top = mRect.left = 0;
        mRect.right = mViewWidth;
        if (mViewWidth < mImageWidth) {
            mScale = mImageWidth / (float) mViewWidth;
        } else {
            mScale = mViewWidth / (float) mImageWidth;
        }
        mRect.bottom = (int) (mImageHeight / mScale);
        //注意缩放的特殊情况，细节1
        if (mScale <= 1) {
            mRect.bottom = mViewHeight;
        }
    }

    /**
     * 第四步，在内容区域画出具体的内容
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitDecoder == null) {
            throw new RuntimeException("请设置图片");
        }
        mOptions.inBitmap = mBitmap;
        mBitmap = mBitDecoder.decodeRegion(mRect, mOptions);
        Matrix matrix = new Matrix();
        matrix.setScale(mScale, mScale);
        canvas.drawBitmap(mBitmap, matrix, null);
    }

    /**
     * 第五步，处理触摸事件，统一交给GestureDector处理
     *
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //直接交给dector处理
        return mGestureDetector.onTouchEvent(event);
    }

    /**
     * 第六步，down事件需要返回true，代表消费此事件
     */
    @Override
    public boolean onDown(MotionEvent e) {
        //触摸时，如果还在滚动，就强制的停止滚动
        if (mScroller != null && !mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        return true;
    }

    /**
     * 第七步，处理滚动，滚动时，需要动态更新mRect并重绘来保证图片对应的区域能绘制到view中
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mRect.offset(0, ((int) distanceY));
        if (mRect.bottom > mImageHeight) {
            mRect.bottom = mImageHeight;
            if (mScale == 1) {
                mRect.bottom = mViewHeight;
            }
            mRect.top = mImageHeight - ((int) (mViewHeight / mScale));
        }
        if (mRect.top < 0) {
            mRect.top = 0;
            mRect.bottom = ((int) (mViewHeight / mScale));
        }
        invalidate();
        return false;
    }

    /**
     * 第八步，处理惯性事件
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mScroller.fling(0, mRect.top, ((int) velocityX), -((int) velocityY), 0, 0, 0, mImageHeight - ((int) (mViewHeight / mScale)));
        return false;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller == null) {
            return;
        }
        if (mScroller.computeScrollOffset()) {
            mRect.top = mScroller.getCurrY();
            mRect.bottom = mScroller.getCurrY() + ((int) (mViewHeight / mScale));
            invalidate();
        }
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.e("test", "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.e("test", "onSingleTapUp");
        return false;
    }


    @Override
    public void onLongPress(MotionEvent e) {
        Log.e("test", "onLongPress");

    }


}

