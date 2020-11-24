package com.weiyu.baselib.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class SquareLayout : LinearLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        setMeasuredDimension(View.getDefaultSize(0, widthMeasureSpec), View.getDefaultSize(0, heightMeasureSpec))
        // Children are just made to fill our space.
        val childWidthSize = measuredWidth
        val childHeightSize = measuredHeight
        //高度和宽度一样
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(childWidthSize, View.MeasureSpec.EXACTLY)
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(childWidthSize, View.MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}