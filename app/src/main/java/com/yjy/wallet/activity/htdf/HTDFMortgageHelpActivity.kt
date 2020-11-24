package com.yjy.wallet.activity.htdf

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_mortgage_info.*


/**
 * weiweiyu
 * 2020/4/9
 * 575256725@qq.com
 * 13115284785
 */

class HTDFMortgageHelpActivity : BaseActivity() {

    override fun getContentLayoutResId(): Int = R.layout.activity_mortgage_info

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        pic_rcv.layoutManager = LinearLayoutManager(this)
        var list = mutableListOf(R.mipmap.htdf_help_01, R.mipmap.htdf_help_02,
                R.mipmap.htdf_help_03, R.mipmap.htdf_help_04, R.mipmap.htdf_help_05,
                R.mipmap.htdf_help_06, R.mipmap.htdf_help_07, R.mipmap.htdf_help_08, R.mipmap.htdf_help_09,
                R.mipmap.htdf_help_10, R.mipmap.htdf_help_11, R.mipmap.htdf_help_12, R.mipmap.htdf_help_13,
                R.mipmap.htdf_help_14, R.mipmap.htdf_help_15, R.mipmap.htdf_help_16, R.mipmap.htdf_help_17,
                R.mipmap.htdf_help_18, R.mipmap.htdf_help_19, R.mipmap.htdf_help_20, R.mipmap.htdf_help_21,
                R.mipmap.htdf_help_22)
        pic_rcv.adapter = object : LQRAdapterForRecyclerView<Int>(this, list, R.layout.adapter_help_item3) {
            override fun convert(helper: LQRViewHolderForRecyclerView?, item: Int, position: Int) {
                helper?.getView<ImageView>(R.id.tv_img)?.setImageResource(item)
            }
        }
    }

}