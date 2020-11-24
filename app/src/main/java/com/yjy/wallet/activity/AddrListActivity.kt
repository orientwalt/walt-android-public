package com.yjy.wallet.activity

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import com.yjy.wallet.bean.Address
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.activity_address_list.*
import org.litepal.LitePal

/**
 *Created by weiweiyu
 *on 2019/6/5
 * 地址本
 */
class AddrListActivity : BaseActivity() {
    var adapter: LQRAdapterForRecyclerView<Address>? = null
    override fun getContentLayoutResId(): Int = R.layout.activity_address_list

    override fun initializeContentViews() {
        var type = intent.getIntExtra("type", -1)
        tb_title.setRightLayoutClickListener(View.OnClickListener { startActivity(AddAddrActivity::class.java) })
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        adapter = object : LQRAdapterForRecyclerView<Address>(this, arrayListOf(), R.layout.adapter_addr_item) {
            override fun convert(helper: LQRViewHolderForRecyclerView, item: Address, position: Int) {
                helper.getView<TextView>(R.id.tv_address).text = item.address
                helper.getView<TextView>(R.id.tv_name).text = item.user
                val iv = helper.getView<ImageView>(R.id.iv_head)
                iv.setImageDrawable(resources.getDrawable(WaltType.values()[item.type].drawable))
                if (TextUtils.isEmpty(item.remarck)) {
                    helper.getView<TextView>(R.id.tv_type).visibility = View.GONE
                } else {
                    helper.getView<TextView>(R.id.tv_type).visibility = View.VISIBLE
                    helper.setText(R.id.tv_type, item.remarck)
                }
                helper.getView<RelativeLayout>(R.id.rl_copy).setOnClickListener {
                    var s = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    s.text = helper.getView<TextView>(R.id.tv_address).text
                    toast(resources.getString(R.string.receive_copy_toast))
                }
                if (type == 0) {
                    helper.setOnItemClickListener { helper, parent, itemView, position ->
                        var intent = Intent()
                        intent.putExtra("sacanurl", item.address)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                } else {
                    helper.setOnItemClickListener { helper, parent, itemView, position ->
                        var intent = Intent(this@AddrListActivity, AddrInfoActivity::class.java)
                        intent.putExtra("data", item)
                        startActivity(intent)
                    }
                }
            }

        }
        rcv_address.layoutManager = LinearLayoutManager(this)
        rcv_address.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (intent.getIntExtra("type", -1) == 0) {
            val waltType = intent.getSerializableExtra("unit") as WaltType
            LitePal.where("type = ? or type= ?", waltType.ordinal.toString(),
                    if (waltType == WaltType.USDT) WaltType.BTC.ordinal.toString() else waltType.ordinal.toString())
                    .order("time desc").findAsync(Address::class.java).listen { list ->
                        if (list.size > 0) {
                            adapter?.data = list
                            tv_null.visibility = View.GONE
                        } else {
                            tv_null.visibility = View.VISIBLE
                        }
                    }
        } else {
            LitePal.order("time desc").findAsync(Address::class.java).listen { list ->
                if (list.size > 0) {
                    adapter?.data = list
                    tv_null.visibility = View.GONE
                } else {
                    adapter?.clearData()
                    tv_null.visibility = View.VISIBLE
                }
            }
        }

    }

}