package com.weiyu.baselib.base.widget

import android.content.Context
import android.view.View
import android.widget.GridView

class NoScroolGridView(context: Context?) : GridView(context) {
    //不出现滚动条
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}