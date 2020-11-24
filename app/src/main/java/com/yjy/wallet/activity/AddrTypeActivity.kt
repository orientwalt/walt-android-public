package com.yjy.wallet.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_address_type.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 */
class AddrTypeActivity : BaseActivity() {
    var adapter: LQRAdapterForRecyclerView<String>? = null
    override fun getContentLayoutResId(): Int = R.layout.activity_address_type

    override fun initializeContentViews() {
        val mPosition = intent.getIntExtra("position", -1)
        val list: Array<String> = when (intent.getIntExtra("type", 0)) {
            3 -> {
                tb_title.setTitle(resources.getString(R.string.type_title3))
                resources.getStringArray(R.array.path_type)
            }
            else -> {
                tb_title.setTitle(resources.getString(R.string.type_title1))
                resources.getStringArray(R.array.sp_language)
            }
        }
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        adapter = object : LQRAdapterForRecyclerView<String>(this, list.toList(), R.layout.adapter_item_type) {
            override fun convert(helper: LQRViewHolderForRecyclerView?, item: String, position: Int) {
                helper?.getView<TextView>(R.id.tv_name)!!.text = item
                if (mPosition == position) {
                    helper?.getView<ImageView>(R.id.iv_check1)!!.visibility = View.VISIBLE
                } else {
                    helper?.getView<ImageView>(R.id.iv_check1)!!.visibility = View.INVISIBLE
                }
                helper?.setOnItemClickListener { helper, parent, itemView, position ->
                    var intent = Intent()
                    intent.putExtra("position", position)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

        }
        rcv_type.layoutManager = LinearLayoutManager(this)
        rcv_type.adapter = adapter

    }

}