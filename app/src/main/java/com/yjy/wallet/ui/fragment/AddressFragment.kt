package com.yjy.wallet.ui.fragment

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
import com.weiyu.baselib.base.LazyFragment
import com.yjy.wallet.R
import com.yjy.wallet.activity.AddrInfoActivity
import com.yjy.wallet.bean.Address
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.fragment_address.*
import org.litepal.LitePal

/**
 * weiweiyu
 * 2020/7/3
 * 575256725@qq.com
 * 13115284785
 */
class AddressFragment : LazyFragment() {

    var adapter: LQRAdapterForRecyclerView<Address>? = null

    override fun getContentLayoutResId(): Int = R.layout.fragment_address

    override fun initializeContentViews() {
        adapter = object : LQRAdapterForRecyclerView<Address>(activity, arrayListOf(), R.layout.adapter_addr_item) {
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
                    var s = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    s.text = helper.getView<TextView>(R.id.tv_address).text
                    (activity as BaseActivity).toast(resources.getString(R.string.receive_copy_toast))
                }
                helper.setOnItemClickListener { helper, parent, itemView, position ->
                    var intent = Intent(activity, AddrInfoActivity::class.java)
                    intent.putExtra("data", item)
                    startActivity(intent)
                }

            }

        }

    }

    override fun viewCreated() {
        rcv_address.layoutManager = LinearLayoutManager(activity)
        rcv_address.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (isVisibles) {
            lazyLoad()
        }
    }

    override fun lazyLoad() {
        var type = arguments?.getString("type")!!
        try {
            val waltType = WaltType.valueOf(type)
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
        } catch (e: Exception) {
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