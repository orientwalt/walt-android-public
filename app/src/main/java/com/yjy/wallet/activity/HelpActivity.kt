package com.yjy.wallet.activity

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_help.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 */
class HelpActivity : BaseActivity() {
    var adapter: LQRAdapterForRecyclerView<String>? = null

    override fun getContentLayoutResId(): Int = R.layout.activity_help

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var list = resources.getStringArray(R.array.what_list)
        adapter = object : LQRAdapterForRecyclerView<String>(this, list.toList(), R.layout.adapter_item_what) {
            override fun convert(helper: LQRViewHolderForRecyclerView?, item: String?, position: Int) {
                helper?.setText(R.id.tv_title, item)
                helper?.setOnItemClickListener { _, _, _, position ->
                    var intent = Intent(this@HelpActivity, WhatActivity::class.java)
                    intent.putExtra("type", position)
                    startActivity(intent)
                }
            }

        }
        rcv_what.layoutManager = LinearLayoutManager(this)
        rcv_what.adapter = adapter
    }

}