package com.yjy.wallet.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.gongwen.marqueen.MarqueeFactory
import com.gongwen.marqueen.SimpleMarqueeView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jaeger.library.StatusBarUtil
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.lqr.adapter.OnItemClickListener
import com.tbruyelle.rxpermissions2.RxPermissions
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.base.BaseFragment
import com.weiyu.baselib.util.*
import com.yjy.wallet.Constant
import com.yjy.wallet.Constant.Companion.allAssetsView
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.R
import com.yjy.wallet.activity.*
import com.yjy.wallet.activity.eth.AssetsActivity2
import com.yjy.wallet.activity.eth.ERCInfoActivity
import com.yjy.wallet.activity.eth.SendETHActivity
import com.yjy.wallet.activity.htdf.HtdfERCActivity
import com.yjy.wallet.activity.htdf.HtdfInfoActivity
import com.yjy.wallet.activity.htdf.SendHtdfActivity
import com.yjy.wallet.activity.other.CoinInfoActivity
import com.yjy.wallet.activity.utxo.AddERCActivity
import com.yjy.wallet.activity.utxo.BTCInfoActivity
import com.yjy.wallet.activity.utxo.SendBTCActivity
import com.yjy.wallet.api.*
import com.yjy.wallet.bean.UpdateW
import com.yjy.wallet.bean.waltbean.InfoItem2
import com.yjy.wallet.bean.waltbean.NoticeBean
import com.yjy.wallet.ui.custom.SpacesItemDecoration
import com.yjy.wallet.utils.CoinUtils
import com.yjy.wallet.wallet.*
import com.zhl.cbdialog.CBDialogBuilder
import de.greenrobot.event.EventBus
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.fragment_index.*
import org.bitcoinj.core.Coin
import java.math.BigDecimal


/**
 *Created by weiweiyu
 *on 2019/4/29
 */
class IndexFragment : BaseFragment() {
    var adapter: LQRAdapterForRecyclerView<WInfo>? = null
    val balance = MutableLiveData<String>()
    var price = 0.0
    var rmbList: MutableList<InfoItem2> = mutableListOf()
    override fun getContentLayoutResId(): Int = R.layout.fragment_index

    override fun initializeContentViews() {
        StatusBarUtil.setLightMode(activity)
        EventBus.getDefault().register(this)
        adapter = object : LQRAdapterForRecyclerView<WInfo>(this.activity, arrayListOf(), R.layout.adapter_coin_item) {
            override fun convert(helper: LQRViewHolderForRecyclerView?, item: WInfo, position: Int) {
                var s1 = if (main) "" else "(test)"
                if (item.unit == "USDT") {
                    s1 = if (item.address.startsWith("0x")) {
                        "(ERC-20)" + if (main) "" else "(test)"
                    } else {
                        "(Omni)" + if (main) "" else "(test)"
                    }
                } else {
                    if (item.address.startsWith("htdf") && item.unit != WaltType.htdf.name) {
                        s1 = "(HRC-20)" + if (main) "" else "(test)"
                    }
                    if (item.address.startsWith("0x") && item.unit != "ETH" && item.unit != "HET" && item.unit != "ETC")
                        s1 = "(ERC-20)" + if (main) "" else "(test)"
                    if (item.address.startsWith("0x") && item.unit != "ETH" && item.unit != "HET" && item.unit != "ETC") {
                        s1 = item.unit.toUpperCase() + "(ERC-20)" + if (main) "" else "(test)"
                    }
                }
                helper?.setText(R.id.tv_name3, s1)
                helper?.setText(R.id.tv_name, item.unit.toUpperCase())
                helper?.setText(R.id.tv_name2, item.address)
                helper?.getView<ImageView>(R.id.iv_icon)!!.setImageDrawable(CoinUtils.getDrawableByType(item, activity!!))
                var rmb = item.rmb.times(item.balance.toDouble())
                var s = if (rmb > 0.0) "≈ ¥ " + Utils.toSubStringDegistForChart(rmb, Constant.rmbP, true) else "--"
                if (allAssetsView) {
                    helper.setText(R.id.tv_rmb, s)
                } else {
                    helper.setText(R.id.tv_rmb, "****")
                }
                val tv_amount = helper.getView<AppCompatTextView>(R.id.tv_price)
                if (!TextUtils.isEmpty(item.balance)) {
                    if (allAssetsView) {
                        tv_amount.text = Utils.toSubStringDegistForChart(item.balance.toDouble(), Constant.priceP, false)
                    } else {
                        tv_amount.text = "****"
                    }
                } else {
                    if (allAssetsView) {
                        tv_amount.text = "0"
                    } else {
                        tv_amount.text = "****"
                    }
                }
                helper.onItemClickListener = OnItemClickListener { _, _, _, _ ->

                    if (item.address.startsWith("htdf") || item.unit == WaltType.htdf.name) {//htdf代币
                        val intent = Intent()
                        intent.setClass(activity!!, HtdfInfoActivity::class.java)
                        intent.putExtra("data", item)
                        startActivity(intent)
                    } else {
                        val intent = Intent()
                        when (item.unit) {
                            WaltType.BTC.name, WaltType.USDT.name, WaltType.BCH.name, WaltType.BSV.name, WaltType.LTC.name, WaltType.DASH.name
                                , WaltType.CXC.name, WaltType.QTUM.name
                            -> {
                                if (item.address.startsWith("0x")) {
                                    intent.setClass(activity!!, ERCInfoActivity::class.java)
                                } else {
                                    intent.setClass(activity!!, BTCInfoActivity::class.java)
                                }
                            }
                            WaltType.usdp.name, WaltType.HET.name, WaltType.XRP.name, WaltType.NEO.name, WaltType.TRX.name, WaltType.XLM.name -> {
                                intent.setClass(activity!!, CoinInfoActivity::class.java)
                            }
                            else -> {
                                intent.setClass(activity!!, ERCInfoActivity::class.java)
                            }
                        }
                        intent.putExtra("data", item)
                        startActivity(intent)
                    }
                }

            }

        }

    }

    fun getInfo(item: WInfo) {
        var rItem = rmbList.find { it.symbol == item.unit.toUpperCase() }
        if (rItem != null) {
            item.rmb = if (TextUtils.isEmpty(rItem.price_cny)) 0.0 else rItem.price_cny.toDouble()
        }
        when (item.unit) {
            WaltType.BSV.name, WaltType.BCH.name, WaltType.BTC.name, WaltType.LTC.name, WaltType.DASH.name, WaltType.ETH.name, WaltType.ETC.name, WaltType.USDT.name -> {
                TokenSendService().balance(item.address, WaltType.valueOf(item.unit))
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (it.code == 1) {
                                item.balance = it.data!!
                            }
                            MyWalletUtils.instance.updateCheckInfo(item)
                        }, {

                        })
            }
            WaltType.usdp.name, WaltType.HET.name -> {
                USDPService(item.unit).getAccount(item.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            var amount = 0.0
                            if (it.value.coins != null && it.value.coins.isNotEmpty()) {
                                for (b in it.value.coins) {
                                    if (item.unit.toLowerCase() == b.denom.toLowerCase())
                                        amount = amount.plus(b.amount.toDouble())
                                }
                            }
                            item.balance = amount.toString()
                            item.sequence = it.value.sequence
                            item.account_number = it.value.account_number
                            MyWalletUtils.instance.updateCheckInfo(item)
                        }, {})
            }
            WaltType.htdf.name -> {
                TxService(WaltType.htdf.name).getAccount(item.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            var data = it.data
                            if (it.err_code == 200 && data != null) {
                                var amount = 0.0
                                if (data.value.coins != null && data.value.coins!!.isNotEmpty()) {
                                    for (b in data.value.coins!!) {
                                        if (item.unit.toLowerCase() == b.denom.toLowerCase())
                                            amount = amount.plus(b.amount.toDouble())
                                    }
                                }
                                item.balance = amount.toString()
                                item.sequence = data.value.sequence
                                item.account_number = data.value.account_number

                            } else {
                                item.balance = "0.0"
                            }
                            MyWalletUtils.instance.updateCheckInfo(item)
                        }, {})

            }
            WaltType.XRP.name -> {
                XRPService.instance.balance(item.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (it.result.status == "success") {
                                item.balance = (it.result.account_data.Balance.toDouble() / 1000000).toString()
                                item.sequence = it.result.account_data.Sequence.toString()
                                item.account_number = it.result.ledger_current_index.toString()
                            } else {
                                item.balance = "0"
                            }
                            MyWalletUtils.instance.updateCheckInfo(item)
                        }, {})
            }
            WaltType.TRX.name -> {
                TRXService.instance.balance(item.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (it.success && it.data.isNotEmpty()) {
                                var data = it.data[0]
                                item.balance = (data.balance.toDouble() / 1000000).toString()
                            } else {
                                item.balance = "0"
                            }
                            MyWalletUtils.instance.updateCheckInfo(item)
                        }, {})
            }
            WaltType.NEO.name -> {
                NEOService.instance.balance(item.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            var balance = 0.0
                            if (it.balance.isNotEmpty()) {
                                it.balance.forEach {
                                    if (it.asset_symbol == item.unit) {
                                        balance += it.amount
                                        item.contract_address = it.asset_hash
                                    }
                                }
                            }

                            item.balance = balance.toString()
                            MyWalletUtils.instance.updateCheckInfo(item)
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

                        }, {})
            }
            WaltType.QTUM.name -> {
                QtumService.instance.balance(item.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (!TextUtils.isEmpty(it)) {
                                item.balance = Coin.valueOf(it.toLong()).toPlainString()
                            }
                            MyWalletUtils.instance.updateCheckInfo(item)
                        }, {})

            }
            WaltType.XLM.name -> {
                XLMService().getAccounts(item.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (it.balances.isNotEmpty()) {
                                item.balance = it.balances[0].balance
                                item.sequence = it.sequenceNumber.toString()
                                MyWalletUtils.instance.updateCheckInfo(item)
                            }
                        }, {})
            }
            else -> {
            }
        }
    }
    @SuppressLint("CheckResult")
    fun getAllERC(fresh: Boolean) {
        val yWallet1 = MyWalletUtils.instance.getCheckWallet() ?: return
        var erc = yWallet1.ercmMap
        if (erc.isEmpty()) {
            return
        }
        var address = ""
        erc.forEach { item ->
            address = item.value?.address!!
        }
        TokenSendService().tokenbalance(address, WaltType.ETH)
                .compose(bindToLifecycle())
                .subscribe({
                    erc.forEach { winfo ->
                        var rItem = rmbList.find { it.symbol == winfo.value!!.unit.toUpperCase() }
                        if (rItem != null) {
                            winfo.value!!.rmb = if (TextUtils.isEmpty(rItem.price_cny)) 0.0 else rItem.price_cny.toDouble()
                        }
                        it.data?.forEach {
                            if (it.tokenInfo.h == winfo.value?.contract_address) {
                                val tokenDecimal = BigDecimal.TEN.pow(winfo.value!!.decimal)
                                val bigDecimal = BigDecimal(it.balance).divide(tokenDecimal).setScale(8, BigDecimal.ROUND_HALF_UP)
                                winfo.value!!.balance = bigDecimal.toString()
                            }
                        }
                        MyWalletUtils.instance.updateCheckInfo(winfo.value!!)
                    }
                    onEvent(null)
                }, { onEvent(null) })
    }

    @SuppressLint("CheckResult")
    fun getAllHRC(fresh: Boolean) {
        val yWallet1 = MyWalletUtils.instance.getCheckWallet() ?: return
        var hrc = yWallet1.htdfercmMap
        if (hrc.isEmpty()) {
            return
        }
        var address = ""
        hrc.forEach { item ->
            address = item.value?.address!!
        }
        TxService(WaltType.htdf.name).tokenBalance(address)
                .compose(bindToLifecycle())
                .subscribe({
                    hrc.forEach { winfo ->
                        var rItem = rmbList.find { it.symbol == winfo.value!!.unit.toUpperCase() }
                        if (rItem != null) {
                            winfo.value!!.rmb = if (TextUtils.isEmpty(rItem.price_cny)) 0.0 else rItem.price_cny.toDouble()
                        }
                        it.data?.forEach {
                            if (winfo.value?.contract_address == it.address) {
                                val tokenDecimal = BigDecimal.TEN.pow(winfo.value!!.decimal)
                                val bigDecimal = BigDecimal(it.amount).divide(tokenDecimal).setScale(8, BigDecimal.ROUND_HALF_UP)
                                winfo.value!!.balance = bigDecimal.toString()
                            }
                        }
                        MyWalletUtils.instance.updateCheckInfo(winfo.value!!)
                    }
                    onEvent(null)
                }, { onEvent(null) })
    }

    val list: MutableList<WInfo?> = arrayListOf()

    fun onEvent(yWallet: YWallet?) {
        adapter!!.clearData()
        list.clear()
        var yWallet1 = MyWalletUtils.instance.getCheckWallet() ?: return
        price = 0.0
        tv_info.setOnClickListener {
            val intent = Intent(activity, WalletInfoActivity::class.java)
            intent.putExtra("data", yWallet1)
            startActivityForResult(intent, 10086)
        }
        title.text = yWallet1.remark
        yWallet1.map.forEach { item ->
            if (item.key != WaltType.EOS.name)
                if (yWallet1.wType == 100) {
                    if (item.value.show) {
                        list.add(item.value)
                        price += item.value.balance.toDouble().times(item.value.rmb)
                        if (yWallet != null) {
                            getInfo(item.value)
                        }
                    }
                } else {
                    list.add(item.value)
                    price += item.value.balance.toDouble().times(item.value.rmb)
                    if (yWallet != null) {
                        getInfo(item.value)
                    }
                }
        }
        yWallet1.wenMap.forEach { item ->
            list.add(item.value)
            price += item.value!!.balance.toDouble().times(item.value!!.rmb)
            if (yWallet != null) {
                getInfo(item.value!!)
            }
        }
        yWallet1.ercmMap.forEach { item ->
            if (!TextUtils.isEmpty(item.value?.contract_address) && item.key == item.value?.contract_address) {
                list.add(item.value)
                price += item.value!!.balance.toDouble().times(item.value!!.rmb)
            } else {
                MyWalletUtils.instance.removeERC(item.key)
            }
        }
        yWallet1.htdfercmMap.forEach { item ->
            if (!TextUtils.isEmpty(item.value?.contract_address) && item.key == item.value?.contract_address) {
                list.add(item.value)
                price += item.value!!.balance.toDouble().times(item.value!!.rmb)
            } else {
                MyWalletUtils.instance.removehtdfERC(item.key)
            }
        }
        if (yWallet != null) {
            getAllERC(true)
            getAllHRC(true)
        }
        val s = if (price > 0.0) "≈ ¥ " + Utils.toSubStringDegistForChart(price, Constant.rmbP, true) else "--"
        if (allAssetsView) {
            tv_price.text = s
        } else {
            tv_price.text = "****"
        }
        adapter?.data = list

    }

    var isshow by PrefUtils("SHOW_GUIDE", false)
//    var isshow = false

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    fun onEvent(update: UpdateW) {
        val yWallet1 = MyWalletUtils.instance.getCheckWallet()
        if ((yWallet1?.wType == 0 && HTDFERCType.values().isNotEmpty()) || yWallet1?.wType == 2 || yWallet1?.wType == 3 || yWallet1?.wType == 100) {
            right_image?.visibility = View.VISIBLE
        } else {
            right_image?.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(yWallet1?.words) && isshow) {
            rl_hint?.visibility = View.VISIBLE
        } else {
            rl_hint?.visibility = View.GONE
        }
        if (MyWalletUtils.instance.getWallet().isNullOrEmpty()) {
            rl_add?.visibility = View.VISIBLE
            rl_index?.visibility = View.GONE
        } else {
            rl_add?.visibility = View.GONE
            rl_index?.visibility = View.VISIBLE
            onEvent(MyWalletUtils.instance.getCheckWallet())
        }
    }

    var marqueeFactory: MyMF? = null
    var marqueeFactory2: MyMF? = null
    override fun viewCreated() {
        marqueeFactory = MyMF(activity!!)
        marqueeFactory2 = MyMF(activity!!)
        (tv_ad2 as SimpleMarqueeView<NoticeBean>).setMarqueeFactory(marqueeFactory)
        (simpleMarqueeView as SimpleMarqueeView<NoticeBean>).setMarqueeFactory(marqueeFactory2)
        (tv_ad2 as SimpleMarqueeView<NoticeBean>).setOnItemClickListener { mView: TextView, mData: NoticeBean, mPosition: Int ->
            ad(mData.title, mData.data)
        }
        (simpleMarqueeView as SimpleMarqueeView<NoticeBean>).setOnItemClickListener { mView: TextView, mData: NoticeBean, mPosition: Int ->
            ad(mData.title, mData.data)
        }
        rl_root.setOnClickListener { }
        rl_top.setOnClickListener { }
        tv_beifen.setOnClickListener {
            show()
        }
        right_image2.setOnClickListener {
            if (RxPermissions(activity!!).isGranted(Manifest.permission.CAMERA)) {
                val intent = Intent(activity, ScanActivity::class.java)
                startActivityForResult(intent, 0x01)
            } else {
                RxPermissions(activity!!).request(Manifest.permission.CAMERA)
                        .subscribe {
                            if (it) {
                                val intent = Intent(activity, ScanActivity::class.java)
                                startActivityForResult(intent, 0x01)
                            } else {
                                toast(resources.getString(R.string.request_permissions))
                            }
                        }
            }
        }
        rcv_coin.layoutManager = LinearLayoutManager(activity)
        rcv_coin.addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelOffset(R.dimen.dp8)))
        tv_eys.setOnClickListener {
            allAssetsView = !allAssetsView
            val s = if (price > 0.0) "≈ ¥ " + Utils.toSubStringDegistForChart(price, Constant.rmbP, true) else "--"
            if (allAssetsView) {
                tv_price.text = s
            } else {
                tv_price.text = "****"
            }
            adapter?.notifyDataSetChanged()
        }
        title.setOnClickListener { startActivity(WalletListActivity::class.java) }
        rcv_coin.adapter = adapter
        srl_coin.setOnRefreshListener {
            onEvent(MyWalletUtils.instance.getCheckWallet())
            getAllERC(false)
            getAllHRC(false)
            Handler().postDelayed({
                srl_coin.isRefreshing = false
            }, 2000)
        }
        right_image.setOnClickListener {
            val mw = MyWalletUtils.instance.getCheckWallet() ?: return@setOnClickListener
            when (mw.wType) {
                0 -> {
                    startActivityForResult(Intent(activity, HtdfERCActivity::class.java), 10086)
                }
                2 -> {
                    startActivityForResult(Intent(activity, AddERCActivity::class.java), 10086)
                }
                3 -> {
                    startActivityForResult(Intent(activity, AssetsActivity2::class.java), 10086)
                }
                100 -> {
                    startActivityForResult(Intent(activity, AssetsActivity::class.java), 10086)
                }
            }
        }
        if (!TextUtils.isEmpty(Constant.mainChainRMB)) {
            rmbList = Gson().fromJson<MutableList<InfoItem2>>(Constant.mainChainRMB, object : TypeToken<MutableList<InfoItem2>>() {}.type)
        }
        onEvent(UpdateW())
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10086) {
                onEvent(UpdateW())
            }
            if (requestCode == 0x01)
                if (data != null) {
                    val type = CoinUtils.address2Type(data.getStringExtra("sacanurl"))
                    val yWallet = MyWalletUtils.instance.getCheckWallet() ?: return
                    val intent = Intent(activity, ScanResultActivity::class.java)
                    intent.putExtra("hasAddress", 1)
                    if (type != null) {
                        when (type) {
                            WaltType.htdf -> {
                                val list = mutableListOf<WInfo>()
                                val w = yWallet.map[type.name]
                                if (w != null && w.show) {
                                    list.add(w)
                                }
                                if (yWallet.htdfercmMap.isNotEmpty()) {
                                    yWallet.htdfercmMap.forEach {
                                        list.add(it.value!!)
                                    }
                                }
                                if (list.size == 1) {
                                    intent.setClass(activity!!, SendHtdfActivity::class.java)
                                    intent.putExtra("data", list[0])
                                } else if (list.size > 1) {
                                    intent.putExtra("type", 2)
                                    intent.putExtra("data", Gson().toJson(list))
                                }
                            }
                            WaltType.EOS -> {
                                val w = yWallet.map[type.name] ?: return
                                if (w.show) {
                                    intent.setClass(activity!!, SendBTCActivity::class.java)
                                    intent.putExtra("data", w)
                                }
                            }
                            WaltType.XRP, WaltType.NEO, WaltType.TRX, WaltType.LTC, WaltType.DASH,
                            WaltType.usdp, WaltType.HET, WaltType.QTUM, WaltType.XLM -> {
                                val w = yWallet.map[type.name] ?: return
                                if (w.show) {
                                    intent.setClass(activity!!, SendBTCActivity::class.java)
                                    intent.putExtra("data", w)
                                }
                            }
                            WaltType.BTC -> {
                                val list = mutableListOf<WInfo>()
                                val btcw = yWallet.map[WaltType.BTC.name]
                                val bsvw = yWallet.map[WaltType.BSV.name]
                                val bchw = yWallet.map[WaltType.BCH.name]
                                val cxcw = yWallet.map[WaltType.CXC.name]
                                val usdtw = yWallet.map[WaltType.USDT.name]
                                if (btcw != null && btcw.show) {
                                    list.add(btcw)
                                }
                                if (usdtw != null && usdtw.show) {
                                    list.add(usdtw)
                                }
                                if (yWallet.wenMap.isNotEmpty()) {
                                    var u = yWallet.wenMap[WaltType.USDT.name]
                                    if (u != null)
                                        list.add(u)
                                }
                                if (bsvw != null && bsvw.show) {
                                    list.add(bsvw)
                                }
                                if (bchw != null && bchw.show) {
                                    list.add(bchw)
                                }
                                if (cxcw != null && cxcw.show) {
                                    list.add(cxcw)
                                }
                                if (list.size == 1) {
                                    intent.setClass(activity!!, SendETHActivity::class.java)
                                    intent.putExtra("data", list[0])
                                } else if (list.size > 1) {
                                    intent.putExtra("type", 2)
                                    intent.putExtra("data", Gson().toJson(list))
                                }
                            }
                            WaltType.ETH -> {
                                val list = mutableListOf<WInfo>()
                                val w = yWallet.map[WaltType.ETH.name]
                                var eW = yWallet.map[WaltType.ETC.name]
                                if (w != null && w.show) {
                                    list.add(w)
                                }
                                if (eW != null && eW.show) {
                                    list.add(eW)
                                }
                                if (yWallet.ercmMap.isNotEmpty()) {
                                    yWallet.ercmMap.forEach {
                                        list.add(it.value!!)
                                    }
                                }
                                if (list.size == 1) {
                                    intent.setClass(activity!!, SendETHActivity::class.java)
                                    intent.putExtra("data", list[0])
                                } else if (list.size > 1) {
                                    intent.putExtra("type", 2)
                                    intent.putExtra("data", Gson().toJson(list))
                                }

                            }
                        }
                    }
                    intent.putExtra("data1", data.getStringExtra("sacanurl"))
                    startActivity(intent)
                }
        }
    }

    fun ad(title: String, content: String) {
        var view = LayoutInflater.from(activity).inflate(R.layout.dialog_ad, null)
        var dialog = CBDialogBuilder(activity, CBDialogBuilder.DIALOG_STYLE_NORMAL, 0.8f).showIcon(false)
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                .setView(view)
                .setDialogBackground(R.color.transparent)
                .showCancelButton(false)
                .setTitle("")
                .showConfirmButton(false)
                .create()
        var tv_title = view.findViewById<TextView>(R.id.tv_ad_title)
        var tv_content = view.findViewById<TextView>(R.id.tv_ad_content)
//        var tv_time = view.findViewById<TextView>(R.id.tv_time)
        view.findViewById<TextView>(R.id.btn_login).setOnClickListener {
            dialog.dismiss()
        }
        tv_title.text = title
        tv_content.text = Html.fromHtml(content)
//        tv_time.text = time
        dialog.findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
        dialog.show()
    }

    fun show() {
        val yWallet = MyWalletUtils.instance.getCheckWallet() ?: return
        val dialog = CBDialogBuilder(activity, CBDialogBuilder.DIALOG_STYLE_NORMAL, 0.8f).showIcon(false)
                .setTouchOutSideCancelable(false)
                .showCancelButton(false)
                .showConfirmButton(false)
                .setTitle("")
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                .setView(R.layout.dialog_pwd)
                .create()
        with(dialog) {
            findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
            val pwd = findViewById<EditText>(R.id.et_pwd)
            this@IndexFragment.setEdittxtForHw(pwd)
            findViewById<RelativeLayout>(R.id.right_layout).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.btn_close).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.btn_sure).setOnClickListener {
                if (TextUtils.isEmpty(pwd.text)) {
                    (activity as BaseActivity).toast(resources.getString(R.string.send_input_pwd))
                    return@setOnClickListener
                }
                if (!StringUtils.isPwd(pwd.text.toString())) {
                    (activity as BaseActivity).toast(resources.getString(R.string.create_pwd_hint))
                    return@setOnClickListener
                }
                if (MD5Util.getMD5String(pwd.text.toString()) != yWallet.pwd) {
                    (activity as BaseActivity).toast(resources.getString(R.string.send_err_pwd))
                    return@setOnClickListener
                }
                Flowable.just("")
                        .compose(bindToLifecycle())
                        .map { AesUtils.decrypt(yWallet.words, pwd.text.toString()) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : ResourceSubscriber<String>() {
                            override fun onComplete() {
                            }

                            override fun onNext(t: String?) {
                                t ?: return
                                val intent = Intent(activity, ExportWordsActivity::class.java)
                                intent.putExtra("words", t)
                                var ylist: MutableList<YWallet> = arrayListOf(yWallet)
                                intent.putExtra("data", Gson().toJson(ylist))
                                startActivity(intent)
                                dismiss()
                            }

                            override fun onError(t: Throwable?) {
                            }
                        })


            }
            show()
        }
    }

}

class MyMF(mContext: Context) : MarqueeFactory<TextView, NoticeBean>(mContext) {
    override fun generateMarqueeItemView(data: NoticeBean): TextView {
        val mView = TextView(mContext)
        mView.text = data.title
        return mView
    }

}