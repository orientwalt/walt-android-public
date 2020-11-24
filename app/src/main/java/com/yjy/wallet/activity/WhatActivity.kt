package com.yjy.wallet.activity

import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_what.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 */
class WhatActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_what

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var list = resources.getStringArray(R.array.what_list)
        var listcontent = resources.getStringArray(R.array.what_list_content)
        var type = intent.getIntExtra("type", 0)
        if (type < list.size) {
            tb_title.setTitle(resources.getString(R.string.help_title))
            tv_title1.text = list[type]
            tv_content.text = listcontent[type]
        }
        webview.setOnLongClickListener { true }
        when (type) {
            Constant.WHAT_100 -> {
                tb_title.setTitle(resources.getString(R.string.about_server))
//                tv_content.text = resources.getString(R.string.about_server)
                webview.visibility = View.VISIBLE
                webview.loadUrl("file:////android_asset/test.html")
            }
            Constant.WHAT_101 -> {
                tb_title.setTitle(resources.getString(R.string.about_privacy))
//                tv_content.text = resources.getString(R.string.about_privacy)
                webview.visibility = View.VISIBLE
                webview.loadUrl("file:////android_asset/test2.html")
            }
            Constant.WHAT_103->{
                tb_title.setTitle(resources.getString(R.string.ad_txt))
                tv_title1.text = resources.getString(R.string.ad_txt)
                tv_content.text = resources.getString(R.string.ad_content)
            }
        }
    }

}
