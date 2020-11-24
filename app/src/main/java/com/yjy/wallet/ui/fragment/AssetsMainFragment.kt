package com.yjy.wallet.ui.fragment

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.weiyu.baselib.base.BaseFragment
import com.yjy.wallet.R
import com.yjy.wallet.bean.ERCBean
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * weiweiyu
 * 2019/8/22
 * 575256725@qq.com
 * 13115284785
 */
class AssetsMainFragment : BaseFragment() {
    var adapter: LQRAdapterForRecyclerView<ERCBean>? = null
    override fun getContentLayoutResId(): Int = R.layout.fragment_search

    override fun initializeContentViews() {
        adapter = object : LQRAdapterForRecyclerView<ERCBean>(activity, mutableListOf(), R.layout.adapter_erc_add_item) {
            override fun convert(helper: LQRViewHolderForRecyclerView, item: ERCBean, position: Int) {
                helper.setText(R.id.tv_name, item.name.toUpperCase())
                val ercType = WaltType.valueOf(item.name)
                helper.getView<ImageView>(R.id.iv_icon)!!.setImageDrawable(resources.getDrawable(ercType.drawable))
                helper.setText(R.id.tv_name2, ercType.fall_name)
                val rlSwitch = helper.getView<RelativeLayout>(R.id.rl_switch)
                rlSwitch.isSelected = item.open
                val tv_bg = helper.getView<TextView>(R.id.tv_bg)
                val tv_left = helper.getView<TextView>(R.id.tv_left)
                val tv_right = helper.getView<TextView>(R.id.tv_right)
                if (rlSwitch.isSelected) {
                    tv_left.visibility = View.GONE
                    tv_right.visibility = View.VISIBLE
                    tv_bg.background = resources.getDrawable(R.drawable.switch_bg)
                } else {
                    tv_left.visibility = View.VISIBLE
                    tv_right.visibility = View.GONE
                    tv_bg.background = resources.getDrawable(R.drawable.switch_bg2)
                }
                rlSwitch.setOnClickListener {
                    MyWalletUtils.instance.showMain(item.name, !rlSwitch.isSelected)
                    if (rlSwitch.isSelected) {
                        tv_left.visibility = View.VISIBLE
                        tv_right.visibility = View.GONE
                        tv_bg.background = resources.getDrawable(R.drawable.switch_bg2)
                    } else {
                        tv_left.visibility = View.GONE
                        tv_right.visibility = View.VISIBLE
                        tv_bg.background = resources.getDrawable(R.drawable.switch_bg)
                    }
                    setdata()
                    activity?.setResult(Activity.RESULT_OK)
                }
            }
        }
    }

    override fun viewCreated() {
        config_hidden.requestFocus()
        rcv_erc.layoutManager = LinearLayoutManager(activity!!)
        rcv_erc.adapter = adapter
        setdata()
    }

    fun setdata() {
        val yWallet = MyWalletUtils.instance.getCheckWallet()
        yWallet?.map?.remove(WaltType.USDT.name)
        yWallet?.map?.remove(WaltType.EOS.name)
        val sList = mutableListOf<ERCBean>()
        yWallet?.map?.forEach { m ->
            val type = WaltType.valueOf(m.key)
            val bean = ERCBean(type.name, type.drawable, "", type.fall_name)
            if (m.value.show) {
                bean.open = m.value.show
            }
            sList.add(bean)
        }
        adapter?.data = sList
    }
}