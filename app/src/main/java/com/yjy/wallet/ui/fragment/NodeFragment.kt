package com.yjy.wallet.ui.fragment

import android.content.Intent
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener
import com.weiyu.baselib.base.ActionSheetDialog
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.base.BaseFragment
import com.weiyu.baselib.util.ImageLoaderManager
import com.weiyu.baselib.util.Utils
import com.weiyu.baselib.widget.RoundLabelTextView
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.htdf.HTDFNodeInfoActivity
import com.yjy.wallet.api.MiningService
import com.yjy.wallet.bean.htdftx.NodeItem
import com.yjy.wallet.utils.Error
import com.yjy.wallet.wallet.WInfo
import kotlinx.android.synthetic.main.fragment_node.*

/**
 * weiweiyu
 * 2020/8/10
 * 575256725@qq.com
 * 13115284785
 */
class NodeFragment : BaseFragment() {
    var adapter: LQRAdapterForRecyclerView<NodeItem>? = null
    var wInfo: WInfo? = null
    override fun getContentLayoutResId(): Int = R.layout.fragment_node

    override fun initializeContentViews() {
        wInfo = arguments?.getSerializable("data") as WInfo
        adapter = object : LQRAdapterForRecyclerView<NodeItem>(activity, arrayListOf(), R.layout.adapter_htdf_node_item) {
            override fun convert(helper: LQRViewHolderForRecyclerView, item: NodeItem, position: Int) {
                var round_text = helper.getView<RoundLabelTextView>(R.id.round_text)
                if (item.identification == 1) {
                    round_text.visibility = View.VISIBLE
                } else {
                    round_text.visibility = View.GONE
                }
                var root = helper.getView<RelativeLayout>(R.id.rl_root)
                if (position % 2 == 0) {
                    root.setBackgroundColor(resources.getColor(R.color.white))
                } else {
                    root.setBackgroundColor(resources.getColor(R.color.bg_f5f5f5))
                }
                if (item.server_logo != "0" && !item.server_logo.startsWith("http")) {
                    item.server_logo = Constant.logopath + item.server_logo
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
                                intent.putExtra("id",id)
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
        tv_go_top.setOnClickListener {
            var behavior = (appbar.layoutParams as CoordinatorLayout.LayoutParams).behavior
            if (behavior is AppBarLayout.Behavior) {
                var appBarLayoutBehavior = behavior
                var topAndBottomOffset = appBarLayoutBehavior.topAndBottomOffset
                if (topAndBottomOffset != 0) {
                    appBarLayoutBehavior.topAndBottomOffset = 0
                }
            }
            rcv_node.scrollToPosition(0)
        }
        tv_sort.setOnClickListener {
            show()
        }
        srl_node.setOnRefreshLoadmoreListener(object : OnRefreshLoadmoreListener {
            override fun onLoadmore(refreshlayout: RefreshLayout?) {
                page++
                getdata(true)
            }

            override fun onRefresh(refreshlayout: RefreshLayout?) {
                page = 1
                getdata(false)
            }
        })
        et_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //点击搜索的时候隐藏软键盘
                hideSoftKeyboard()
                showProgressDialog("")
                page = 1
                getdata(false)
                true
            } else {
                false
            }
        }
        rcv_node.layoutManager = LinearLayoutManager(activity)
        rcv_node.adapter = adapter
        showProgressDialog("")
        getdata(false)
    }

    fun getdata(load: Boolean) {
        MiningService().getserver(page, pageSize, position + 1, et_search.text.toString().trim().toLowerCase(), wInfo!!.address)
                .compose(bindToLifecycle())
                .subscribe({
                    if (load) {
                        srl_node.finishLoadmore()
                    } else {
                        srl_node.finishRefresh()
                    }
                    if (it.data != null) {
                        all = it.data!!.count
                        if (load) {
                            adapter?.addMoreData(it.data!!.node)
                        } else {
                            adapter?.data = it.data!!.node
                        }
                    }
                    dismissProgressDialog()
                }, {
                    if (load) {
                        srl_node.finishLoadmore()
                    } else {
                        srl_node.finishRefresh()
                    }
                    Error.error(it, activity as BaseActivity)
                })
    }

    var page = 1
    var pageSize = 20
    var all = 0.0

    var position = 0
    fun show() {
        val strs = resources.getStringArray(R.array.sort)
        val dialog = ActionSheetDialog(activity)
                .builder()
                .setTitle(resources.getString(R.string.node_orderby))
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
        for (i in strs.indices) {
            var s = strs[i]
            dialog.addSheetItem(s, if (i == position) R.color.title_bg_5f8def else R.color.txt_333333) {
                position = it - 1
                page = 1
                showProgressDialog("")
                getdata(false)
            }
        }
        dialog.show()
    }
}