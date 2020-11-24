package com.yjy.wallet.activity.other

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
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.Utils
import com.weiyu.baselib.widget.ThumbnailView
import com.yjy.wallet.Constant
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.R
import com.yjy.wallet.activity.ReceiveActivity
import com.yjy.wallet.api.*
import com.yjy.wallet.bean.LoadEvent
import com.yjy.wallet.bean.StopEvent
import com.yjy.wallet.utils.CoinUtils
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import com.zhl.cbdialog.CBDialogBuilder
import de.greenrobot.event.EventBus
import kotlinx.android.synthetic.main.activity_address_info.*


/**
 *Created by weiweiyu
 *on 2019/4/29
 */
class CoinInfoActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_address_info
    var fragments: MutableList<Fragment> = ArrayList()
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    lateinit var wallet: WInfo
    override fun initializeContentViews() {
        EventBus.getDefault().register(this)
        title_tb.setLeftLayoutClickListener(View.OnClickListener { finish() })
        wallet = intent.getSerializableExtra("data") as WInfo
        tv_wen.setOnClickListener {
            show()
        }
        tv_address.text = wallet.address
        tv_address.setOnClickListener {
            var cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var mClipData = ClipData.newPlainText("Label", tv_address.text)
            cm.setPrimaryClip(mClipData)
            toast(resources.getString(R.string.receive_copy_toast))
        }
        iv_logo.setImageDrawable(CoinUtils.getDrawableByType(wallet, this))
        title_tb.setTitle(wallet.unit.toUpperCase() + if (main) "" else "(test)")
        btn_send.setOnClickListener {
            val intent = Intent(this@CoinInfoActivity, SendActivity::class.java)
            intent.putExtra("data", wallet)
            startActivity(intent)
        }
        srl_coin.setOnRefreshLoadmoreListener(object : OnRefreshLoadmoreListener {
            override fun onLoadmore(refreshlayout: RefreshLayout?) {
                EventBus.getDefault().post(LoadEvent(true, xTablayout.selectedTabPosition))
//                refreshlayout?.finishLoadmore(3000/*,false*/)

            }

            override fun onRefresh(refreshlayout: RefreshLayout?) {
                getBalance()
                EventBus.getDefault().post(LoadEvent(false, xTablayout.selectedTabPosition))
//                refreshlayout?.finishRefresh(3000/*,false*/)
            }
        })
        var params = appbar.layoutParams as CoordinatorLayout.LayoutParams
        var behavior = params.behavior as AppBarLayout.Behavior
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(p0: AppBarLayout): Boolean {
                return true
            }
        })
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 -> srl_coin.isEnabled = p1 >= 0 })
        btn_receive.setOnClickListener {
            val intent = Intent(this@CoinInfoActivity, ReceiveActivity::class.java)
            intent.putExtra("data", wallet)
            startActivity(intent)
        }
        var titles = resources.getStringArray(R.array.transfer_type).toMutableList()
        for (i in titles.indices) {
            var fragment = OtherFragment()
            var b = Bundle()
            b.putSerializable("data", wallet)
            b.putInt("type", i)
            fragment.arguments = b
            fragments.add(fragment)
            var tab = xTablayout.newTab()
            tab.text = titles[i]
            xTablayout.addTab(tab)
        }
        xTablayout.addOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: XTabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {

            }

            override fun onTabSelected(tab: XTabLayout.Tab?) {
                srl_coin.finishRefresh()
            }
        })
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

    var page = 1
    fun getBalance() {
        when (WaltType.valueOf(wallet.unit)) {
            WaltType.usdp, WaltType.HET -> {
                USDPService(wallet.unit).getAccount(wallet.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (it.value.coins!!.isNotEmpty()) {
                                var amount = 0.0
                                for (b in it.value.coins) {
                                    if (wallet.unit.toLowerCase() == b.denom.toLowerCase())
                                        amount = amount.plus(b.amount.toDouble())
                                }
                                wallet.balance = amount.toString()
                            }
                            wallet.sequence = it.value.sequence
                            wallet.account_number = it.value.account_number
                            MyWalletUtils.instance.updateCheckInfo(wallet)
                            setRmb()
                        }, {})
            }
            WaltType.XRP -> {
                XRPService.instance.balance(wallet.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (it.result.status == "success") {
                                wallet.balance = (it.result.account_data.Balance.toDouble() / 1000000).toString()
                                wallet.sequence = it.result.account_data.Sequence.toString()
                                wallet.account_number = it.result.ledger_current_index.toString()
                            } else {
                                wallet.balance = "0"
                            }
                            MyWalletUtils.instance.updateCheckInfo(wallet)
                            setRmb()
                        }, {})

            }
            WaltType.TRX -> {
                TRXService.instance.balance(wallet.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (it.success && it.data.isNotEmpty()) {
                                var data = it.data[0]
                                wallet.balance = (data.balance.toDouble() / 1000000).toString()
                            } else {
                                wallet.balance = "0"
                            }
                            MyWalletUtils.instance.updateCheckInfo(wallet)
                            setRmb()
                        }, {})

            }
            WaltType.XLM -> {
                XLMService().getAccounts(wallet.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (it.balances.isNotEmpty()) {
                                wallet.balance = it.balances[0].balance
                                wallet.sequence = it.sequenceNumber.toString()
                                MyWalletUtils.instance.updateCheckInfo(wallet)
                            }
                            setRmb()
                        }, {})

            }
            WaltType.NEO -> {
                NEOService.instance.balance(wallet.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            var balance = 0.0
                            if (it.balance.isNotEmpty()) {
                                it.balance.forEach {
                                    if (it.asset_symbol == wallet.unit) {
                                        balance += it.amount
                                        wallet.contract_address = it.asset_hash
                                    }
                                }
                            }

                            wallet.balance = balance.toString()
                            MyWalletUtils.instance.updateCheckInfo(wallet)
                            setRmb()
                        }, {})
            }
        }

    }

    override fun onResume() {
        super.onResume()
        wallet = MyWalletUtils.instance.getCheckWallet()!!.map[wallet.unit]!!
        getBalance()
        setRmb()
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
        var rmb = wallet.rmb.times(wallet.balance.toDouble())
        if (rmb == 0.0) {
            tv_rmb.text = "--"
        } else
            tv_rmb.text = "≈ ¥ " + Utils.toSubStringDegistForChart(rmb, Constant.rmbP, true)
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
