package com.weiyu.baselib.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.weiyu.baselib.R

class TitleBar(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {
    //    private var leftLayout: RelativeLayout
    private var leftImage: ThumbnailView
    private var rightLayout: RelativeLayout
    private var rightImage: ThumbnailView
     var titleView: TextView
    private var titleLayout: RelativeLayout
    private var unread_msg_number: TextView
    private var rightTxt: TextView
    private var rightTxtLayout: RelativeLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_title_bar, this)
//        leftLayout = findViewById(R.id.left_layout)
        leftImage = findViewById(R.id.left_image)
        rightLayout = findViewById(R.id.right_layout)
        rightImage = findViewById(R.id.right_image)
        titleView = findViewById(R.id.title)
        titleLayout = findViewById(R.id.root)
        rightTxt = findViewById(R.id.right_txt)
        rightTxtLayout = findViewById(R.id.right_txt_layout)
        unread_msg_number = findViewById(R.id.unread_msg_number)
        parseStyle(context, attrs)
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun parseStyle(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar)
            val title = ta.getString(R.styleable.TitleBar_titleBarTitle)
            titleView.text = title

            val leftDrawable = ta.getDrawable(R.styleable.TitleBar_titleBarLeftImage)
            if (null != leftDrawable) {
                leftImage.setImageDrawable(leftDrawable)
                leftImage.visibility = View.VISIBLE
            }
            val rightDrawable = ta.getDrawable(R.styleable.TitleBar_titleBarRightImage)
            if (null != rightDrawable) {
                rightImage.setImageDrawable(rightDrawable)
                rightImage.visibility = View.VISIBLE
            }
            val background = ta.getDrawable(R.styleable.TitleBar_titleBarBackground)
            if (null != background) {
                titleLayout.background = background
            }

            ta.recycle()
        }
    }

    fun setTitleImg(@DrawableRes id: Int) {
        titleView.setBackgroundResource(id)
    }

    fun setLeftImageResource(@DrawableRes resId: Int) {
        leftImage.setImageDrawable(resources.getDrawable(resId))
        leftImage.visibility = View.VISIBLE
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun setRightImageResource(@DrawableRes resId: Int) {
        rightImage.setImageDrawable(resources.getDrawable(resId))
        rightImage.visibility = View.VISIBLE
    }

    fun setUnread_msg_numberV(visibility: Int) {
        unread_msg_number.visibility = visibility
    }

    fun setLeftLayoutClickListener(listener: View.OnClickListener) {
        leftImage.setOnClickListener(listener)
    }

    fun setRightLayoutClickListener(listener: View.OnClickListener) {
        rightTxtLayout.setOnClickListener(listener)
        rightImage.setOnClickListener(listener)
    }

    fun setLeftLayoutVisibility(visibility: Int) {
        leftImage.visibility = visibility
    }

    fun setRightLayoutVisibility(visibility: Int) {
        rightLayout.visibility = visibility
    }

    fun setTitle(title: String) {
        titleView.text = title
    }

    fun setTitleTextColor(@ColorRes color: Int) {
        titleView.setTextColor(context.resources.getColor(color))
    }

    fun setRightText(rightText: String) {
        rightTxt.text = rightText
        rightTxtLayout.visibility = View.VISIBLE
    }

    fun setRightTextColor(@ColorRes color: Int) {
        rightTxt.setTextColor(context.resources.getColor(color))
    }

    fun setRightTextSize(size: Float) {
        rightTxt.textSize = size
    }

    override fun setBackgroundColor(@ColorRes color: Int) {
        titleLayout.setBackgroundColor(color)
    }

}