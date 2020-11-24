package com.yjy.wallet.activity.htdf

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
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
import com.yjy.wallet.api.TxService
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.bean.htdftx.Trade
import com.yjy.wallet.utils.TimeUtils
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.activity_htdf_income.*

/**
 * weiweiyu
 * 2020/5/18
 * 575256725@qq.com
 * 13115284785
 */
class HtdfInComeActivity : BaseActivity() {

    var adapter: LQRAdapterForRecyclerView<Trade>? = null
    override fun getContentLayoutResId(): Int = R.layout.activity_htdf_income

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        adapter = object : LQRAdapterForRecyclerView<Trade>(this, arrayListOf(), R.layout.adapter_transations_item) {
            override fun convert(helper: LQRViewHolderForRecyclerView?, item: Trade, position: Int) {
                val tvPrice = helper?.getView<TextView>(R.id.tv_price)
                helper?.setText(R.id.tv_exchang_time, TimeUtils.stampToDate(TimeUtils.dateToStamp(item.tradetime)))
                val tvState = helper?.getView<TextView>(R.id.tv_state)
                if (item.inval == 0) {
                    tvState?.text = resources.getString(R.string.coininfo_fail)
                    tvState?.setTextColor(Color.RED)
                } else {
                    tvState?.text = resources.getString(R.string.coininfo_success)
                    tvState?.setTextColor(resources.getColor(R.color.txt_8f95a9))
                }
                var p = Utils.toSubStringDegistForChart(
                        if (TextUtils.isEmpty(item.money)) 0.0 else item.money.toDouble(),
                        Constant.priceP,
                        false
                )
                helper?.setText(R.id.tv_address, item.to)
                helper?.setText(
                        R.id.tv_price,
                        "+$p HTDF"
                )
                tvPrice?.setTextColor(resources.getColor(R.color.btn_bg_5f8def))
                helper?.itemView?.setOnClickListener {
                    var txItem = TxItem(
                            item.blockheight.toString(),
                            item.tradehash.toLowerCase(),
                            if (item.contract_type == "5") item.to else item.from,
                            if (item.contract_type == "5") item.from else item.to,
                            p,
                            item.type,
                            item.memo,
                            TimeUtils.dateToStamp(item.tradetime),
                            Constant.main
                    )
                    txItem.type = item.contract_type
                    txItem.state = when (item.inval) {
                        1 -> 1
                        0 -> 2
                        else -> 0
                    }
                    val intent = Intent(this@HtdfInComeActivity, HtdfTxInfoActivity::class.java)
                    var w = getIntent().getSerializableExtra("w") as WInfo
                    intent.putExtra("data", txItem)
                    intent.putExtra("walt", w)
                    if (txItem.To == w.address) {
                        intent.putExtra("type", "+")
                    } else {
                        intent.putExtra("type", "-")
                    }
                    startActivity(intent)
                }
            }
        }
        rcv_income.layoutManager = LinearLayoutManager(this)
        rcv_income.adapter = adapter
        srl_coin.setOnRefreshLoadmoreListener(
                object : OnRefreshLoadmoreListener {
                    override fun onLoadmore(refreshlayout: RefreshLayout?) {
                        page++
                        getdata(true)
                    }

                    override fun onRefresh(refreshlayout: RefreshLayout?) {
                        page = 1
                        getdata(false)
                    }
                })
        srl_coin.autoRefresh()

    }

    @SuppressLint("CheckResult")
    fun getdata(load: Boolean) {
        TxService(WaltType.htdf.name).blocklog(intent.getStringExtra("address"), page.toString(), "2", intent.getStringExtra("vaddress"))
                .compose(bindToLifecycle())
                .subscribe({
                    if (load) {
                        adapter?.addMoreData(it.data)
                        srl_coin.finishLoadmore()
                    } else {
                        adapter?.data = it.data
                        srl_coin.finishRefresh()
                    }
                }, {
                    if (load) {
                        srl_coin.finishLoadmore()
                    } else {
                        srl_coin.finishRefresh()
                    }
                })
    }

    var page = 1
}