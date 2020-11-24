package com.weiyu.baselib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.weiyu.baselib.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * weiweiyu
 * 2020/7/20
 * 575256725@qq.com
 * 13115284785
 */
public class RotateTrackView extends View {

    /**
     * 起始点横坐标
     */
    private float startXPoint;

    /**
     * 起始点纵坐标
     */
    private float startYPoint;

    /**
     * 宽度
     */
    private float width;

    /**
     * 高度
     */
    private float heigth;

    /**
     * 圆角半径
     */
    private float radius;

    /**
     * 图形点集合
     */
    float[] mPoints;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 标志位
     */
    private final int LeftUp = 0;
    private final int LeftDown = 1;
    private final int RightDown = 2;
    private final int RightUp = 3;

    /**
     * 停止动画标志位
     */
    private boolean isPause = false;

    /**
     * 动画开始位置起点
     */
    private int indexBegin = 0;
    /**
     * 动画结束位置，也就是整个动画的长度
     */
    private int indexEnd = 200;

    private int LENGTH = 200;

    private int TIME = 1;

    /**
     * 截取的轨迹点集合
     */
    private float[] snackPoints;

    public RotateTrackView(Context context) {
        super(context);
    }

    public RotateTrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateTrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RotateTrackView(Context context, float startXPoint, float startYPoint, float width, float heigth, float radius) {
        this(context);
        this.startXPoint = startXPoint;
        this.startYPoint = startYPoint;
        this.heigth = heigth;
        this.width = width;
        this.radius = radius;

        // 初始化画笔
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.colorPrimary));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);

        // 轨迹计算
        ArrayList<Float> mPointLists = new ArrayList<>();
        addXPoints(width, radius, startXPoint, startYPoint, mPointLists);
        addCircleArgle(RightUp, radius, mPointLists);
        addYPoints(heigth, radius, mPointLists.get(mPointLists.size() - 1), mPointLists.get(mPointLists.size() - 2), mPointLists);
        addCircleArgle(RightDown, radius, mPointLists);
        reduceXPoints(width, radius, mPointLists.get(mPointLists.size() - 2), mPointLists.get(mPointLists.size() - 1), mPointLists);
        addCircleArgle(LeftDown, radius, mPointLists);
        reduceYPoints(heigth, radius, mPointLists.get(mPointLists.size() - 1), mPointLists.get(mPointLists.size() - 2), mPointLists);
        addCircleArgle(LeftUp, radius, mPointLists);

        // 将list集合转换成canvas接受的float数组
        mPoints = new float[mPointLists.size()];
        for (int i = 0; i < mPointLists.size(); i++) {
            mPoints[i] = mPointLists.get(i);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (snackPoints.length == LENGTH) {
            canvas.drawPoints(snackPoints, mPaint);
        }
    }

    /**
     * x轴向右计算
     *
     * @param length
     * @param radius
     * @param addNumber
     * @param constantNumber
     * @param list
     */
    private void addXPoints(float length, float radius, float addNumber, float constantNumber, List<Float> list) {
        for (int i = 1; i < (length - 2 * radius); i++) {
            float pointX = addNumber + i;
            float pointY = constantNumber;
            list.add(pointX);
            list.add(pointY);
        }
    }

    private void reduceXPoints(float length, float radius, float addNumber, float constantNumber, List<Float> list) {
        for (int i = 1; i < (length - 2 * radius); i++) {
            float pointX = addNumber - i;
            float pointY = constantNumber;
            list.add(pointX);
            list.add(pointY);
        }
    }

    private void addYPoints(float length, float radius, float addNumber, float constantNumber, List<Float> list) {
        for (int i = 1; i < (length - 2 * radius); i++) {
            float pointX = addNumber + i;
            float pointY = constantNumber;
            list.add(pointY);
            list.add(pointX);
        }
    }

    private void reduceYPoints(float length, float radius, float addNumber, float constantNumber, List<Float> list) {
        for (int i = 1; i < (length - 2 * radius); i++) {
            float pointX = addNumber - i;
            float pointY = constantNumber;
            list.add(pointY);
            list.add(pointX);
        }
    }

    /**
     * 由于上下左右四个圆的轨迹坐标计算方式是不一样的，需要单独处理
     *
     * @param position
     * @param radius
     * @param list
     */
    private void addCircleArgle(int position, float radius, List<Float> list) {
        int argle = 0;
        float x = list.get(list.size() - 2);
        float y = list.get(list.size() - 1);
        for (int i = 0; i < 90; i += 10) {
            argle = i + 10;

            switch (position) {

                case RightUp:
                    float x1 = (float) (x + Math.sin((Math.PI / 180) * argle) * radius);
                    float y1 = (float) (y + (radius - Math.cos((Math.PI / 180) * argle) * radius));
                    list.add(x1);
                    list.add(y1);
                    break;

                case RightDown:
                    float x2 = (float) (x - (radius - Math.cos((Math.PI / 180) * argle) * radius));
                    float y2 = (float) (y + (Math.sin((Math.PI / 180) * argle) * radius));
                    list.add(x2);
                    list.add(y2);
                    break;

                case LeftDown:
                    float x3 = (float) (x - (Math.sin((Math.PI / 180) * argle) * radius));
                    float y3 = (float) (y - (radius - Math.cos((Math.PI / 180) * argle) * radius));
                    list.add(x3);
                    list.add(y3);
                    break;

                case LeftUp:
                    float x4 = (float) (x + (radius - Math.cos((Math.PI / 180) * argle) * radius));
                    float y4 = (float) (y - (Math.sin((Math.PI / 180) * argle) * radius));
                    list.add((float) (x + (radius - Math.cos((Math.PI / 180) * argle) * radius)));
                    list.add((float) (y - (Math.sin((Math.PI / 180) * argle) * radius)));
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 开始动画
     */
    public void startAnim() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (!isPause) {
                    RotateTrackView.this.post(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                        }
                    });
                    indexBegin += 2;
                    indexEnd += 2;
                    if (indexEnd < mPoints.length) {
                        snackPoints = (Arrays.copyOfRange(mPoints, indexBegin, indexEnd));
                    } else if (indexBegin == mPoints.length) {
                        indexBegin = 0;
                        indexEnd = LENGTH;
                    } else {
                        float[] floats2 = Arrays.copyOfRange(mPoints, indexBegin, mPoints.length);
                        float[] floats3 = Arrays.copyOf(floats2, LENGTH);
                        int length = floats2.length;
                        for (int i = 0; i < (LENGTH - floats2.length); i++) {
                            floats3[length] = mPoints[i];
                            length++;
                        }
                        snackPoints = floats3;

                    }
                    try {
                        Thread.sleep(TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    /**
     * 停止动画
     */
    public void stopAnim() {
        isPause = true;
    }

    /**
     * 设置轨迹粗细
     */
    public void setLinesWidth(int width) {
        mPaint.setStrokeWidth(width);
    }
}