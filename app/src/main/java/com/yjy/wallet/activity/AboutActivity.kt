package com.yjy.wallet.activity

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.update.SysUtils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_about.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 */
class AboutActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_about

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        tv_version.text = String.format(resources.getString(R.string.about_version), SysUtils.getAppVersionName(this))
//        var mRequestOptions = RequestOptions.circleCropTransform()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
//                .skipMemoryCache(true)//不做内存缓存
//        Glide.with(this).load(R.mipmap.ic_launcher).apply(mRequestOptions).into(iv_logo)
    }

    fun onClickWhat(v: View) {
        var intent = Intent(this@AboutActivity, WhatActivity::class.java)
        when (v.id) {
            R.id.rl_server -> {
                intent.putExtra("type", Constant.WHAT_100)
            }
            R.id.rl_privacy -> {
                intent.putExtra("type", Constant.WHAT_101)
            }
        }
        startActivity(intent)
    }

    fun check(v: View) {

    }

}