package com.weiyu.baselib.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;

/**
 * weiweiyu
 * 2019/7/29
 * 575256725@qq.com
 * 13115284785
 */
public class ThumbnailView extends AppCompatImageView {
    public ThumbnailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //在按下事件中设置滤镜
                setFilter();
                break;
            case MotionEvent.ACTION_UP:
                //由于捕获了Touch事件，需要手动触发Click事件
                performClick();
            case MotionEvent.ACTION_CANCEL:
                //在CANCEL和UP事件中清除滤镜
                removeFilter();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 设置滤镜
     */
    private void setFilter() {
        AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation
        this.startAnimation(alpha);
        //先获取设置的src图片
//        Drawable drawable = getDrawable();
//        //当src图片为Null，获取背景图片
//        if (drawable == null) {
//            drawable = getBackground();
//        }
//        if (drawable != null) {
//            //设置滤镜
//            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
//        }
    }

    /**
     * 清除滤镜
     */
    private void removeFilter() {
        AlphaAnimation alpha = new AlphaAnimation(1F, 1F);
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation
        this.startAnimation(alpha);
        //先获取设置的src图片
//        Drawable drawable = getDrawable();
//        //当src图片为Null，获取背景图片
//        if (drawable == null) {
//            drawable = getBackground();
//        }
//        if (drawable != null) {
//            //清除滤镜
//            drawable.clearColorFilter();
//        }
    }


}
