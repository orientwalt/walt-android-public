package com.yjy.wallet.activity.utxo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.androidkun.xtablayout.XTabLayout
import com.jaeger.library.StatusBarUtil
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.Utils
import com.weiyu.baselib.widget.ThumbnailView
import com.yjy.wallet.Constant
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.R
import com.yjy.wallet.activity.ReceiveActivity
import com.yjy.wallet.api.CXCService
import com.yjy.wallet.api.QtumService
import com.yjy.wallet.api.TokenSendService
import com.yjy.wallet.bean.LoadEvent
import com.yjy.wallet.bean.StopEvent
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.utils.CoinUtils
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import com.zhl.cbdialog.CBDialogBuilder
import de.greenrobot.event.EventBus
import kotlinx.android.synthetic.main.activity_btc_info.*
import org.bitcoinj.core.Coin


/**
 *Created by weiweiyu
 *on 2019/4/29
 */
class BTCInfoActivity : BaseActivity() {
    var page = 1
    override fun getContentLayoutResId(): Int = R.layout.activity_btc_info
    var fragments: MutableList<Fragment> = ArrayList()
    var adapter: LQRAdapterForRecyclerView<TxItem>? = null
    lateinit var wallet: WInfo

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initializeContentViews() {
        EventBus.getDefault().register(this)
        title_tb.setTitleTextColor(R.color.txt_333333)
        title_tb.setLeftLayoutClickListener(View.OnClickListener { finish() })
        wallet = intent.getSerializableExtra("data") as WInfo
        tv_address.text = wallet.address
        tv_address.setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", tv_address.text)
            cm.setPrimaryClip(mClipData)
            toast(resources.getString(R.string.receive_copy_toast))
        }
        tv_wen.setOnClickListener {
            show()
        }
        iv_logo.setImageDrawable(CoinUtils.getDrawableByType(wallet, this))
        title_tb.setTitle(wallet.unit.toUpperCase() + if (main) "" else "(test)")
        btn_send.setOnClickListener {
            var intent = Intent(this@BTCInfoActivity, SendBTCActivity::class.java)
            intent.putExtra("data", wallet)
            startActivity(intent)
        }
        srl_coin.setOnRefreshLoadmoreListener(object : OnRefreshLoadmoreListener {
            override fun onLoadmore(refreshlayout: RefreshLayout?) {
                EventBus.getDefault().post(LoadEvent(true, xTablayout.selectedTabPosition))
//                refreshlayout?.finishLoadmore(3000/*,false*/)

            }

            override fun onRefresh(refreshlayout: RefreshLayout?) {
                gebalance(wallet)
                EventBus.getDefault().post(LoadEvent(false, xTablayout.selectedTabPosition))
//                refreshlayout?.finishRefresh(3000/*,false*/)
            }
        })
        btn_receive.setOnClickListener {
            val intent = Intent(this@BTCInfoActivity, ReceiveActivity::class.java)
            intent.putExtra("data", wallet)
            startActivity(intent)
        }
        var titles = resources.getStringArray(R.array.transfer_type).toMutableList()
        for (i in titles.indices) {
            var fragment = UTXOInfoFragment()
            var b = Bundle()
            b.putSerializable("data", wallet)
            b.putInt("type", i)
            fragment.arguments = b
            fragments.add(fragment)
            var tab = xTablayout.newTab()
            tab.text = titles[i]
            xTablayout.addTab(tab)
        }
        var params = appbar.layoutParams as CoordinatorLayout.LayoutParams
        var behavior = params.behavior as AppBarLayout.Behavior
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(p0: AppBarLayout): Boolean {
                return true
            }
        })
        xTablayout.addOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: XTabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {

            }

            override fun onTabSelected(tab: XTabLayout.Tab?) {
                srl_coin.finishRefresh()
            }
        })
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 -> srl_coin.isEnabled = p1 >= 0 })
        viewpager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getCount(): Int {
                return fragments.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles[position].toUpperCase()
            }
        }
        xTablayout.setupWithViewPager(viewpager)

    }

    fun onEvent(stop: StopEvent) {
        srl_coin.finishRefresh()
    }

    fun gebalance(item: WInfo) {
        when (item.unit) {
            WaltType.BSV.name, WaltType.BCH.name, WaltType.BTC.name, WaltType.LTC.name, WaltType.DASH.name, WaltType.USDT.name -> {
                TokenSendService().balance(wallet.address, WaltType.valueOf(wallet.unit))
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (it.code == 1) {
                                item.balance = it.data!!
                                MyWalletUtils.instance.updateCheckInfo(item)
                            }
                            setRmb()
                        }, {

                        })
            }
            WaltType.QTUM.name -> {
                QtumService.instance.balance(item.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (!TextUtils.isEmpty(it)) {
                                item.balance = Coin.valueOf(it.toLong()).toPlainString()
                            }
                            MyWalletUtils.instance.updateCheckInfo(item)
                            setRmb()
                        }, {})

            }
            WaltType.CXC.name -> {
                CXCService().getbalance(item.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (it.data != null) {
                                item.balance = it.data!!
                                MyWalletUtils.instance.updateCheckInfo(item)
                            }
                            setRmb()
                        }, {})
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setRmb()
        gebalance(wallet)
    }

    fun setRmb() {
        if (!TextUtils.isEmpty(wallet.balance) and !TextUtils.isEmpty(wallet.unit)) {
            tv_amount.text = Utils.toSubStringDegistForChart(
                    wallet.balance.toDouble(),
                    Constant.priceP,
                    false
            )
        } else {
            tv_amount.text = "0"
        }
        val rmb = wallet.rmb.times(wallet.balance.toDouble())
        if (rmb == 0.0) {
            tv_rmb.text = "--"
        } else
            tv_rmb.text = "≈ ¥ " + Utils.toSubStringDegistForChart(rmb, Constant.rmbP, true)
    }


    override fun setStatusBar() {
        super.setStatusBar()
        StatusBarUtil.setLightMode(this)
        StatusBarUtil.setColor(this, resources.getColor(R.color.white), 0)
    }

    fun show() {
        var view = LayoutInflater.from(this).inflate(R.layout.dialog_transfer, null)
        var dialog = CBDialogBuilder(this, CBDialogBuilder.DIALOG_STYLE_NORMAL, 0.8f).showIcon(false)
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                .setView(view)
                .showCancelButton(false)
                .setTitle("")
                .showConfirmButton(false)
                .create()
        var tv_close = view.findViewById<ThumbnailView>(R.id.tv_close)
        tv_close.setOnClickListener {
            dialog.dismiss()
        }
        var title = view.findViewById<TextView>(R.id.tv_title)
        var tv_title = view.findViewById<TextView>(R.id.tv_ad_title)
        tv_title.text = resources.getString(R.string.tranfer_hint4)
        var tv_content = view.findViewById<TextView>(R.id.tv_ad_content)
        tv_content.text = resources.getString(R.string.tranfer_hint5)
        dialog.findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
        dialog.show()
    }
}
