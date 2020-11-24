package com.yjy.wallet.activity.htdf

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.Utils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.api.MiningService
import com.yjy.wallet.bean.HtdfApp
import com.yjy.wallet.bean.htdftx.MyNodeInfo
import com.yjy.wallet.bean.htdftx.NodeItem
import com.yjy.wallet.utils.Error
import com.yjy.wallet.wallet.WInfo
import kotlinx.android.synthetic.main.activity_htdf_user_history.*


/**
 * weiweiyu
 * 2020/4/8
 * 575256725@qq.com
 * 13115284785
 * 华特东方超级节点详细信息
 */

class HTDFUserHistoryActivity : BaseActivity() {
    var adapter: LQRAdapterForRecyclerView<HtdfApp>? = null
    var data1: NodeItem? = null
    var w: WInfo? = null
    override fun getContentLayoutResId(): Int = R.layout.activity_htdf_user_history

    @SuppressLint("CheckResult")
    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        w = intent.getSerializableExtra("data") as WInfo
        data1 = intent.getSerializableExtra("item") as NodeItem

        tv_weituo.setOnClickListener {
            val intent = Intent(this, HTDFUserMortgateLogActivity::class.java)
            intent.putExtra("w", w)
            intent.putExtra("data", data1)
            startActivity(intent)
        }
        srl_coin.setOnRefreshLoadmoreListener(object : OnRefreshLoadmoreListener {
            override fun onLoadmore(refreshlayout: RefreshLayout?) {
                page++
                getList(true)
            }

            override fun onRefresh(refreshlayout: RefreshLayout?) {
                page = 1
                getList(false)
            }
        })
        ttt.setOnClickListener {
            val intent = Intent(this, HtdfInComeActivity::class.java)
            intent.putExtra("address", w!!.address)
            intent.putExtra("w", w)
            intent.putExtra("vaddress", data1!!.server_address)
            startActivity(intent)
        }
        var arr = resources.getStringArray(R.array.htdf_state)
        adapter = object : LQRAdapterForRecyclerView<HtdfApp>(this, arrayListOf(), R.layout.adapter_htdf_app_item) {
            override fun convert(helper: LQRViewHolderForRecyclerView, item: HtdfApp, position: Int) {
                var tvState = helper.getView<TextView>(R.id.tv_state)
                var tvState2 = helper.getView<TextView>(R.id.tv_state2)
                if (item.type < arr.size) {
                    tvState.text = arr[item.type]
                }
                when (item.type) {
                    0, 1 -> tvState.setTextColor(resources.getColor(R.color.txt_333333))
                    2, 6 -> {
                        tvState.setTextColor(resources.getColor(R.color.red))
                    }
                    3, 4, 5, 7 -> tvState.setTextColor(resources.getColor(R.color.green))
                }
                when (item.type) {
                    2 -> {
                        tvState2.text = resources.getString(R.string.commission_node_hint44)
                    }
                    3 -> {
                        tvState2.text = resources.getString(R.string.commission_node_hint43)
                        tvState2.setOnClickListener {
                            var p = Utils.toSubStringDegistForChart(item.amount.toDouble(), 8, false).replace(",", "")
                            var intent = Intent(this@HTDFUserHistoryActivity, HtdfUnMortgageActivity::class.java)
                            intent.putExtra("data", w)
                            intent.putExtra("item", data1)
                            intent.putExtra("price", p)
                            intent.putExtra("id", item.id.toString())
                            startActivityForResult(intent, 0)
                        }
                    }
                    else -> tvState2.text = ""
                }
                var s = Utils.toSubStringDegistForChart(item.amount.toDouble(), Constant.priceP, false)
                helper.setText(R.id.tv_address, String.format(resources.getString(R.string.commission_node_hint41), item.time))
                helper.setText(R.id.tv_exchang_time, String.format(resources.getString(R.string.commission_node_hint42), s))
                helper.itemView.setOnClickListener {
                    var i = Intent(this@HTDFUserHistoryActivity, LiftHtdfActivity::class.java)
                    i.putExtra("data", item)
                    startActivity(i)
                }
            }
        }
        rcv_app.layoutManager = LinearLayoutManager(this)
        rcv_app.adapter = adapter
        srl_coin.autoRefresh()
    }

    var page = 1
    var limit = 20
    fun getList(load: Boolean) {
        MiningService().history_detail(data1!!.server_address, w!!.address, page.toString(), limit.toString())
                .compose(bindToLifecycle())
                .subscribe({
                    if (load) {
                        adapter?.addMoreData(it.data?.log)
                    } else {
                        adapter?.data = it.data?.log
                    }
                    var p = Utils.toSubStringDegistForChart((it.data?.total!!.toDouble()), Constant.priceP, false)
                    var p2 = Utils.toSubStringDegistForChart((it.data?.sy_total!!.toDouble()), Constant.priceP, false)
                    tv_my_price.text = p + "HTDF"
                    tv_my_price2.text = p2 + "HTDF"
                    srl_coin.finishRefresh()
                    srl_coin.finishLoadmore()
                }, {
                    srl_coin.finishRefresh()
                    srl_coin.finishLoadmore()
                    Error.error(it, this)
                })
    }

    var isPause = false

    override fun onResume() {
        super.onResume()
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    fun setData(data: MyNodeInfo) {
        var p = Utils.toSubStringDegistForChart((data.shares.toDouble() / 100000000), Constant.priceP, false)
        tv_my_price.text = p
        ll_my.visibility = View.VISIBLE
        var s = data.profit.toDouble().div(100000000)
        var p2 = Utils.toSubStringDegistForChart(s, Constant.priceP, false)
        tv_my_price2.text = p2
    }


}