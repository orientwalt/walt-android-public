package com.yjy.wallet.ui.fragment

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.weiyu.baselib.base.BaseFragment
import com.weiyu.baselib.user.User
import com.weiyu.baselib.user.UserUtil
import com.weiyu.baselib.util.Utils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.*
import com.yjy.wallet.activity.htdf.HtdfNodeActivity
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WaltType
import com.yjy.wallet.wallet.YWallet
import com.zhl.cbdialog.CBDialogBuilder
import de.greenrobot.event.EventBus
import kotlinx.android.synthetic.main.fragment_my.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 * 我的页面
 */
class MyFragment : BaseFragment() {
    override fun getContentLayoutResId(): Int = R.layout.fragment_my

    override fun initializeContentViews() {
        EventBus.getDefault().register(this)
    }

    fun onEvent(user: User) {
        if (!user.isLogin) {
            UserUtil.instance.remove()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun viewCreated() {
        rl_root.setOnClickListener { }
        rl_about.setOnClickListener { start(it) }
        rl_setting.setOnClickListener { start(it) }
        rl_msg.setOnClickListener { start(it) }
        rl_help.setOnClickListener { start(it) }
        rl_address.setOnClickListener { start(it) }
    }

    private fun start(v: View) {
        when (v.id) {
            R.id.rl_about -> startActivity(AboutActivity::class.java)
            R.id.rl_setting -> startActivity(SettingActivity::class.java)
            R.id.rl_help -> startActivity(HelpActivity::class.java)
            R.id.rl_msg -> {
                var w = MyWalletUtils.instance.getCheckHTDFWallet()
                if (w != null) {
                    var wInfo = w.map[WaltType.htdf.name]
                    if (wInfo != null) {
                        val intent = Intent()
                        intent.setClass(activity!!, HtdfNodeActivity::class.java)
                        intent.putExtra("data", wInfo)
                        startActivity(intent)
                    } else {
                        showList()
                    }
                } else {
                    showList()
                }
            }
            R.id.rl_address -> startActivity(AddrListActivity2::class.java)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    fun showList() {
        var data = MyWalletUtils.instance.getHTDFWallet()
        var view = LayoutInflater.from(activity).inflate(R.layout.dialog_walt, null)
        var dialog = CBDialogBuilder(activity, CBDialogBuilder.DIALOG_STYLE_NORMAL, 1f).showIcon(false)
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_BOTTOM)
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setView(view)
                .showCancelButton(false)
                .setTitle("")
                .showConfirmButton(false)
                .create()
        var rcv_wallet = view.findViewById<RecyclerView>(R.id.rcv_wallet)
        var rl_null = view.findViewById<RelativeLayout>(R.id.rl_null)
        if (data.isEmpty()) {
            rl_null.visibility = View.VISIBLE
        }
        view.findViewById<TextView>(R.id.btn_login).setOnClickListener {
            startActivity(AddCoinActivity::class.java)
        }
        var adapter = object : LQRAdapterForRecyclerView<YWallet>(activity, arrayListOf(), R.layout.adapter_wallet_list_item3) {
            override fun convert(helper: LQRViewHolderForRecyclerView?, item: YWallet, position: Int) {
                var check = helper?.getView<RelativeLayout>(R.id.rl_check)
                if (item.htdf) {
                    check?.visibility = View.VISIBLE
                } else {
                    check?.visibility = View.GONE
                }
                val mostValue = item.map[WaltType.htdf.name]
                helper?.setText(R.id.tv_name2, mostValue!!.address)
                helper?.setText(R.id.tv_name, item.remark)
                helper?.setText(R.id.tv_price, Utils.toSubStringDegistForChart(mostValue!!.balance.toDouble(), Constant.priceP, false) + mostValue!!.unit.toUpperCase())
                helper?.setOnItemClickListener { _, _, _, _ ->
                    dialog.dismiss()
                    MyWalletUtils.instance.updateHTDF(item)
                }
            }
        }
        rcv_wallet.layoutManager = LinearLayoutManager(activity)
        rcv_wallet.adapter = adapter
        adapter.data = data
        dialog.findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
        dialog.show()
    }
}