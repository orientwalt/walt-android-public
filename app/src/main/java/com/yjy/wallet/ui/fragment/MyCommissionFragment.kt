package com.yjy.wallet.ui.fragment

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
import com.weiyu.baselib.base.BaseFragment
import com.weiyu.baselib.util.ImageLoaderManager
import com.weiyu.baselib.util.Utils
import com.weiyu.baselib.widget.RoundLabelTextView
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.htdf.HTDFNodeInfoActivity
import com.yjy.wallet.api.MiningService
import com.yjy.wallet.api.TxService
import com.yjy.wallet.bean.htdftx.NodeItem
import com.yjy.wallet.utils.Error
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.fragment_my_commission.*

/**
 * weiweiyu
 * 2020/8/10
 * 575256725@qq.com
 * 13115284785
 */
class MyCommissionFragment : BaseFragment() {

    var adapter: LQRAdapterForRecyclerView<NodeItem>? = null
    var data: List<NodeItem> = mutableListOf()
    var all = 0.0
    var wInfo: WInfo? = null
    override fun getContentLayoutResId(): Int = R.layout.fragment_my_commission
    var show = false
    override fun initializeContentViews() {
        wInfo = arguments?.getSerializable("data") as WInfo
        adapter = object : LQRAdapterForRecyclerView<NodeItem>(activity, arrayListOf(), R.layout.adapter_htdf_node_item) {
            override fun convert(helper: LQRViewHolderForRecyclerView, item: NodeItem, position: Int) {
                var root = helper.getView<RelativeLayout>(R.id.rl_root)
                if (position % 2 == 0) {
                    root.setBackgroundColor(resources.getColor(R.color.white))
                } else {
                    root.setBackgroundColor(resources.getColor(R.color.bg_f5f5f5))
                }
                if (item.server_logo != "0" && !item.server_logo.startsWith("http")) {
                    item.server_logo = Constant.logopath + item.server_logo
                }
                var round_text = helper.getView<RoundLabelTextView>(R.id.round_text)
                if (show) {
                    round_text.visibility = View.VISIBLE
                } else {
                    round_text.visibility = View.GONE
                }
                var rank = helper.getView<TextView>(R.id.tv_rank)
                rank.text = item.rank.toString()
                helper.setText(R.id.tv_name, item.server_name)
                helper.setText(R.id.tv_num, item.node_num.toString() + resources.getString(R.string.unit_people))
                helper.setText(R.id.tv_token, "${Utils.toSubStringDegistForChart(item.node_total, 4, false)}HTDF")
                var s = Utils.toSubStringDegistForChart((900000 / (10000 + all)).times(1 - item.proportion), 4, false)
                helper.setText(R.id.tv_percent, Utils.StringToPecent(s.toDouble()))
                var id = R.mipmap.node_icon1
                if (TextUtils.isEmpty(item.server_logo) || item.server_logo == "0") {
                    id = if (position % 2 == 0) {
                        R.mipmap.node_icon1
                    } else {
                        R.mipmap.node_icon2
                    }
                    helper.getView<ImageView>(R.id.iv_left)?.setImageResource(id)
                } else
                    ImageLoaderManager.loadCircleImage(activity, item.server_logo, helper.getView(R.id.iv_left), id)
                helper.itemView.setOnClickListener {
                    var w = arguments?.getSerializable("data") as WInfo
                    showProgressDialog("")
                    MiningService().getinfo(w.address, item.server_address)
                            .compose(bindToLifecycle())
                            .subscribe({
                                dismissProgressDialog()
                                var data = it.data
                                var intent = Intent(activity, HTDFNodeInfoActivity::class.java)
                                intent.putExtra("item", item)
                                intent.putExtra("data", w)
                                intent.putExtra("pecent", s)
                                intent.putExtra("id", id)
                                if (it.code == 200 && data != null) {
                                    dismissProgressDialog()
                                    intent.putExtra("info", data)
                                    intent.putExtra("type", 0)
                                } else {
                                    intent.putExtra("type", 1)
                                }
                                startActivity(intent)
                            }, {
                                Error.error(it, activity as BaseActivity)
                            })
                }
            }
        }
    }

    override fun viewCreated() {
        rcv_node.layoutManager = LinearLayoutManager(activity)
        rcv_node.adapter = adapter
        var w = MyWalletUtils.instance.getCheckHTDFWallet()
        if (w != null) {
            var wInfo = w.map[WaltType.htdf.name] ?: return
            tv_name.text = w.remark
            tv_address.text = wInfo.address
            setPrice()
        }
        showProgressDialog("")
        srl_node.setOnRefreshListener {
            getdata(wInfo!!.address)
        }
        getdata(wInfo!!.address)
    }

    fun getdata(address: String) {
        TxService(WaltType.htdf.name).getAccount(address)
                .compose(bindToLifecycle())
                .subscribe({
                    var data = it.data
                    if (it.err_code == 200 && data != null) {
                        var amount = 0.0
                        if (data.value.coins != null && data.value.coins!!.isNotEmpty()) {
                            for (b in data.value.coins!!) {
                                if (wInfo!!.unit.toLowerCase() == b.denom.toLowerCase())
                                    amount = amount.plus(b.amount.toDouble())
                            }
                        }
                        wInfo?.balance = amount.toString()
                    } else {
                        wInfo?.balance = "0.0"
                    }
                    MyWalletUtils.instance.updateCheckInfo(wInfo!!)
                    setPrice()
                }, { setPrice() })
        MiningService().myServer(address)
                .compose(bindToLifecycle())
                .subscribe({
                    srl_node.finishRefresh()
                    if (it.data != null) {
                        if (it.data != null) {
                            tv_num.text = it.data!!.nodenum.toString()
                            if (it.data!!.nodenum == 0) {
                                tv_tuijian.visibility = View.VISIBLE
                            } else {
                                show = true
                            }
                            tv_all.text = Utils.toSubStringDegistForChart((it.data!!.nodetotal.toDouble() / 100000000), Constant.priceP, false) + "HTDF"
                            tv_in.text = Utils.toSubStringDegistForChart(it.data!!.nodeprofit.toDouble(), Constant.priceP, false) + "HTDF"
                            all = it.data!!.count
                            adapter?.data = it.data!!.node
                        }
                    }
                    dismissProgressDialog()
                }, {
                    srl_node.finishRefresh()
                    Error.error(it, activity as BaseActivity)
                })
    }

    fun setPrice() {
        tv_price.text = Utils.toSubStringDegistForChart(wInfo?.balance!!.toDouble(), Constant.priceP, false)
        var rmb = wInfo!!.rmb.times(wInfo?.balance!!.toDouble())
        var s = if (rmb > 0.0) "≈ ¥ " + Utils.toSubStringDegistForChart(rmb, Constant.rmbP, true) else "--"
        tv_rmb.text = s
    }
}