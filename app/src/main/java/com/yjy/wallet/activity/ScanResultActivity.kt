package com.yjy.wallet.activity

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import com.yjy.wallet.activity.eth.SendETHActivity
import com.yjy.wallet.activity.utxo.SendBTCActivity
import com.yjy.wallet.bean.ERCBean
import com.yjy.wallet.wallet.ERCType
import com.yjy.wallet.wallet.HTDFERCType
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.activity_scan_result.*
import org.litepal.LitePal

/**
 * weiweiyu
 * 2019/8/29
 * 575256725@qq.com
 * 13115284785
 */
class ScanResultActivity : BaseActivity() {
    var adapter: LQRAdapterForRecyclerView<ERCBean>? = null
    var list = mutableListOf<WInfo>()
    override fun getContentLayoutResId(): Int = R.layout.activity_scan_result

    override fun initializeContentViews() {
        val str = intent.getStringExtra("data1")
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        adapter = object : LQRAdapterForRecyclerView<ERCBean>(this, mutableListOf(), R.layout.adapter_coin_item2) {
            override fun convert(helper: LQRViewHolderForRecyclerView, item: ERCBean, position: Int) {
                helper.setText(R.id.tv_name, item.name.toUpperCase())
                helper.getView<ImageView>(R.id.iv_icon)!!.setImageDrawable(resources.getDrawable(item.drawable!!))
                helper.setText(R.id.tv_name2, item.fullName)
                helper.setOnItemClickListener { helper, parent, itemView, position ->
                    var intent = Intent()
                    try {
                        val ercType = WaltType.valueOf(item.name)
                        intent.setClass(this@ScanResultActivity, SendBTCActivity::class.java)
                    } catch (e: java.lang.Exception) {
                        intent.setClass(this@ScanResultActivity, SendETHActivity::class.java)
                    }
                    intent.putExtra("data1", str)
                    intent.putExtra("data", list[position])
                    intent.putExtra("hasAddress", 1)
                    startActivity(intent)
                }
                if (position == adapter?.data?.size!! - 1) {
                    helper.getView<View>(R.id.v_line).visibility = View.INVISIBLE
                } else {
                    helper.getView<View>(R.id.v_line).visibility = View.VISIBLE
                }
            }
        }
        rcv_coin.layoutManager = LinearLayoutManager(this)
        rcv_coin.adapter = adapter

        val type = intent.getIntExtra("type", 0)
        tv_content.text = str
        if (type == 1) {
            list = Gson().fromJson<MutableList<WInfo>>(intent.getStringExtra("data"), object : TypeToken<MutableList<WInfo>>() {}.type)
            val beanlist = mutableListOf<ERCBean>()
            list.forEach {
                val type = WaltType.valueOf(it.unit)
                val bean = ERCBean(type.name, type.drawable, "", type.fall_name)
                beanlist.add(bean)
            }
            adapter?.data = beanlist
        }
        if (type == 2) {
            list = Gson().fromJson<MutableList<WInfo>>(intent.getStringExtra("data"), object : TypeToken<MutableList<WInfo>>() {}.type)
            val beanlist = mutableListOf<ERCBean>()
            list.forEach {
                try {
                    val waltType = WaltType.valueOf(it.unit)
                    val bean = ERCBean(waltType.name, waltType.drawable, "", waltType.fall_name)
                    beanlist.add(bean)
                } catch (e: Exception) {
                    if (it.address.startsWith("0x")) {
                        try {
                            val ercType = ERCType.valueOf(it.unit)
                            val bean = ERCBean(ercType.name, ercType.drawable, ercType.address, ercType.fall_name)
                            beanlist.add(bean)
                        } catch (e: Exception) {
                            val bean = LitePal.where("name = ? and type = ?", it.unit, WaltType.ETH.ordinal.toString()).findFirst(ERCBean::class.java)
                                    ?: return
                            beanlist.add(bean)
                        }
                    } else if (it.address.startsWith("htdf")) {
                        try {
                            val ercType = HTDFERCType.valueOf(it.unit)
                            val bean = ERCBean(ercType.name, ercType.drawable, ercType.address, ercType.fall_name)
                            beanlist.add(bean)
                        } catch (e: Exception) {
                            val bean = LitePal.where("name = ? and type = ?", it.unit, WaltType.htdf.ordinal.toString()).findFirst(ERCBean::class.java)
                                    ?: return
                            beanlist.add(bean)
                            beanlist.add(bean)
                        }

                    }
                }
            }
            adapter?.data = beanlist
        }
    }

}