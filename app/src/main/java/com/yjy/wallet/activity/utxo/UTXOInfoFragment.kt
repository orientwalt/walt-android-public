package com.yjy.wallet.activity.utxo

import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.weiyu.baselib.base.LazyFragment
import com.weiyu.baselib.util.Utils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.api.CXCService
import com.yjy.wallet.api.QtumService
import com.yjy.wallet.api.TokenSendService
import com.yjy.wallet.bean.LoadEvent
import com.yjy.wallet.bean.StopEvent
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.utils.TimeUtils
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import de.greenrobot.event.EventBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.fragment_info.*
import org.bitcoinj.core.Coin
import org.litepal.LitePal

/**
 * weiweiyu
 * 2020/7/16
 * 575256725@qq.com
 * 13115284785
 */
class UTXOInfoFragment : LazyFragment() {
    var adapter: LQRAdapterForRecyclerView<TxItem>? = null
    var wallet: WInfo? = null
    var type = 0
    fun onEvent(load: LoadEvent) {
        if (load.type == type)
            if (isVisibles)
                if (load.isLoad) {
                    page++
                    getDataList(wallet!!, true)
                } else {
                    page = 1
                    getDataList(wallet!!, false)
                }
    }

    override fun refreshLoad() {
        super.refreshLoad()
        page = 1
        getDb(false, type)
    }


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()

    }

    override fun getContentLayoutResId(): Int = R.layout.fragment_info

    override fun initializeContentViews() {
        EventBus.getDefault().register(this)
        wallet = arguments?.getSerializable("data") as WInfo
        type = arguments?.getInt("type") ?: 0
        adapter = object : LQRAdapterForRecyclerView<TxItem>(activity, arrayListOf(), R.layout.adapter_transations_item) {
            override fun convert(helper: LQRViewHolderForRecyclerView?, item: TxItem, position: Int) {
                var icon = helper?.getView<ImageView>(R.id.iv_icon)
                val tvPrice = helper?.getView<AppCompatTextView>(R.id.tv_price)
                helper?.setText(R.id.tv_exchang_time, TimeUtils.stampToDate(item.Time!!))
                val tvState = helper?.getView<TextView>(R.id.tv_state)
                when (item.state) {
                    2 -> {
                        tvState?.text = resources.getString(R.string.coininfo_fail)
                        tvState?.setTextColor(Color.RED)
                    }
                    1 -> {
                        tvState?.text = resources.getString(R.string.coininfo_success)
                        tvState?.setTextColor(resources.getColor(R.color.txt_8f95a9))
                    }
                    0 -> {
                        tvState?.text = ""
                        tvState?.setTextColor(resources.getColor(R.color.txt_8f95a9))
                    }
                }
                if (item.To == wallet?.address) {
                    icon?.setImageDrawable(resources.getDrawable(R.mipmap.transfer_in))
                    helper?.setText(
                            R.id.tv_price,
                            "+${Utils.toSubStringDegistForChart(
                                    item.amount!!.toDouble(),
                                    Constant.priceP,
                                    false
                            )} ${item.denom?.toUpperCase()}"
                    )
                    tvPrice?.setTextColor(resources.getColor(R.color.btn_bg_5f8def))
                    helper?.setText(R.id.tv_address, item.From)
                } else {
                    icon?.setImageDrawable(resources.getDrawable(R.mipmap.transfer_out))
                    helper?.setText(
                            R.id.tv_price,
                            "-${Utils.toSubStringDegistForChart(
                                    item.amount!!.toDouble(),
                                    Constant.priceP,
                                    false
                            )} ${item.denom?.toUpperCase()}"
                    )
                    tvPrice?.setTextColor(resources.getColor(R.color.txt_ff0000))
                    helper?.setText(R.id.tv_address, item.To)
                }
                helper?.setOnItemClickListener { _, _, _, _ ->
                    val intent = Intent(activity, BTCTxInfoActivity::class.java)
                    intent.putExtra("data", item)
                    intent.putExtra("walt", wallet)
                    if (item.To == wallet?.address) {
                        intent.putExtra("type", "+")
                    } else {
                        intent.putExtra("type", "-")
                    }
                    startActivity(intent)
                }

            }
        }
    }

    override fun viewCreated() {
        rcv_record.layoutManager = LinearLayoutManager(activity)
        rcv_record.adapter = adapter
        srl_coin.setOnLoadmoreListener {
            page++
            getDataList(wallet!!, true)
        }
        if (type == 0) {
            lazyLoad()
        }
        refreshLoad()
    }

    override fun lazyLoad() {
        getDb(false, type)
    }

    var page = 1
    var pageSize = 10
    fun getDataList(wallet: WInfo, load: Boolean) {
        var s = object : ResourceSubscriber<MutableList<TxItem>>() {
            override fun onComplete() {
            }

            override fun onNext(it: MutableList<TxItem>) {
                getDb(load, type)
                srl_coin.finishLoadmore()
                hideBar()
            }

            override fun onError(t: Throwable?) {
                getDb(load, type)
                srl_coin.finishLoadmore()
                hideBar()
            }
        }
        when (WaltType.valueOf(wallet.unit)) {
            WaltType.BSV, WaltType.BCH, WaltType.BTC, WaltType.LTC, WaltType.DASH, WaltType.USDT -> {
                var list: MutableList<TxItem> = mutableListOf()
                TokenSendService().getUTxs(wallet.address, page.toString(), WaltType.valueOf(wallet.unit), pageSize.toString())
                        .observeOn(Schedulers.io())
                        .compose(bindToLifecycle())
                        .map { data ->
                            data.data?.forEach { ttx ->
                                ttx.txs?.forEach { tutx ->
                                    var from = ""
                                    var to = ""
                                    var isMe = false
                                    var outvalue = 0.0
                                    var invalue = 0.0
                                    tutx.inputs?.forEach {
                                        if (it.address == wallet.address) {
                                            isMe = true
                                            from = wallet.address
                                        }
                                    }
                                    if (!isMe) {
                                        tutx.inputs?.forEach {
                                            from = it.address
                                        }
                                    }
                                    for (i in tutx.outputs!!.indices) {
                                        var o = tutx.outputs!![i]
                                        if (!TextUtils.isEmpty(o.address)) {
                                            if (o.address == wallet.address && !isMe) {//自己钱包输出
                                                invalue = o.value.toDouble()
                                                to = wallet.address
                                            }
                                            if (o.address != wallet.address && isMe) {
                                                outvalue += o.value.toDouble()
                                                to = o.address
                                            }
                                            if (isMe && i == 0) {
                                                to = o.address
                                            }
                                        }
                                    }
                                    val value = if (isMe) outvalue else invalue
                                    var txItem = TxItem(
                                            tutx.height.toString(),
                                            tutx.txid.toLowerCase(),
                                            from,
                                            to,
                                            value.toString(),
                                            wallet.unit,
                                            "",
                                            tutx.time * 1000,
                                            Constant.main
                                    )
                                    txItem.comfirm = tutx.confirmations
                                    if (tutx.confirmations >= 6) {
                                        txItem.state = 1
                                    } else {
                                        txItem.state = 0
                                    }
                                    txItem.fee = Coin.parseCoin(tutx.fee).value
//                                    var lItem = LitePal.where("Hash = ? and denom = ?", tutx.txid.toLowerCase(), wallet.unit).findFirst(TxItem::class.java)
//                                    if (lItem == null || lItem.state != 1) {
                                    txItem.saveOrUpdate("Hash = ? and denom = ?", tutx.txid.toLowerCase(), wallet.unit)
//                                    }

                                }
                            }
                            list
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s)
            }
            WaltType.CXC -> {
                CXCService().gettradelist(wallet.address, wallet.hight.toString(), page, pageSize)
                        .observeOn(Schedulers.io())
                        .compose(bindToLifecycle())
                        .map {
                            var list: MutableList<TxItem> = mutableListOf()
                            it.data?.forEach { item ->
                                var fee = 0L
                                var toValue = 0.0
                                var fromValue = 0.0
                                var from = ""
                                var to = ""
                                var invalue = 0.0
                                var outvalue = 0.0
                                var isMe = false
                                item.vin?.forEach { cutxo ->
                                    if (!TextUtils.isEmpty(cutxo.address)) {
                                        fromValue += cutxo.value.toDouble()
                                        if (cutxo.address == wallet.address) {
                                            isMe = true
                                            from = cutxo.address
                                        }
                                    }
                                }
                                if (!isMe) {
                                    item.vin?.forEach { cutxo ->
                                        from = cutxo.address
                                    }
                                }
                                var i = 0
                                item.vout?.forEach { cutxo ->
                                    if (!TextUtils.isEmpty(cutxo.address)) {
                                        toValue += cutxo.value.toDouble()
                                        if (!isMe && cutxo.address == wallet.address) {
                                            to = wallet.address
                                            invalue = cutxo.value.toDouble()
                                        }
                                        if (cutxo.address != wallet.address && isMe) {
                                            outvalue += cutxo.value.toDouble()
                                        }
                                        if (isMe && i == 0) {
                                            to = cutxo.address
                                        }
                                    }
                                    i++
                                }
                                fee = (fromValue * 1000000).toLong() - (toValue * 1000000).toLong()
                                var txItem = TxItem(
                                        item.blockheight.toString(),
                                        item.txhash.toLowerCase(),
                                        from,
                                        to,
                                        if (isMe) outvalue.toString() else invalue.toString(),
                                        wallet.unit,
                                        "",
                                        item.time * 1000,
                                        Constant.main
                                )
                                txItem.fee = fee
                                txItem.state = 1
                                wallet.hight = item.blockheight
//                                var lItem = LitePal.where("Hash = ? and denom = ?", item.txhash.toLowerCase(), wallet.unit).findFirst(TxItem::class.java)
//                                if (lItem == null || lItem.state != 1) {
                                txItem.saveOrUpdate("Hash = ? and denom = ?", item.txhash.toLowerCase(), wallet.unit)
//                                }
                            }
                            list
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s)
            }
            WaltType.QTUM -> {
                QtumService.instance.txs(wallet.address)
                        .observeOn(Schedulers.io())
                        .compose(bindToLifecycle())
                        .map {
                            var list: MutableList<TxItem> = mutableListOf()
                            it.items.forEach {
                                var from = ""
                                var to = ""
                                var isMe = false
                                var invalue = 0.0
                                var outvalue = 0.0
                                it.vin.forEach {
                                    if (it.addr == wallet.address) {
                                        isMe = true
                                        from = wallet.address
                                    }
                                }
                                if (!isMe) {
                                    it.vin.forEach {
                                        from = it.addr
                                    }
                                }
                                var i = 0
                                it.vout.forEach {
                                    if (!isMe && it.scriptPubKey.addresses!![0] == wallet.address) {
                                        to = wallet.address
                                        invalue += it.value.toDouble()
                                    }
                                    if (it.scriptPubKey.addresses!![0] != wallet.address && isMe) {
                                        outvalue += it.value.toDouble()
                                    }
                                    if (isMe && i == 0) {
                                        to = it.scriptPubKey.addresses[0]
                                    }
                                    i++
                                }
                                var height = if (it.blockheight == -1) {
                                    0
                                } else {
                                    it.blockheight
                                }
                                var txItem = TxItem(
                                        height.toString(),
                                        it.txid.toLowerCase(),
                                        from,
                                        to,
                                        if (isMe) outvalue.toString() else invalue.toString(),
                                        wallet.unit,
                                        "",
                                        it.time * 1000,
                                        Constant.main
                                )
                                txItem.comfirm = it.confirmations
                                txItem.state = 0
                                if (txItem.comfirm > 0) {
                                    txItem.state = 1
                                }
                                txItem.fee = (it.fees * 100000000).toLong()

//                                var lItem = LitePal.where("Hash = ? and denom = ?", it.txid.toLowerCase(), wallet.unit).findFirst(TxItem::class.java)
//                                if (lItem == null || lItem.state != 1) {
                                txItem.saveOrUpdate("Hash = ? and denom = ?", it.txid.toLowerCase(), wallet.unit)
//                                }
                            }
                            list
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s)
            }
        }
    }

    var first = true
    var show = false
    fun setData(load: Boolean, list: MutableList<TxItem>) {
//        if (load) {
//            adapter?.addMoreData(list)
//        } else {
            adapter?.data = list
//        }
        if (adapter?.data.isNullOrEmpty()) {
            tv_null.visibility = View.VISIBLE
            show = true
        } else {
            tv_null.visibility = View.GONE
        }
        if (first) {
            first = false
            if (show)
                showBar()
            getDataList(wallet!!, false)
        }
    }

    fun getDb(load: Boolean, type: Int) {
        if (this.type != type) {
            return
        }
        EventBus.getDefault().post(StopEvent())
        when (type) {
            0 -> {
                LitePal.where("(From = ? or To = ?) and isMain = ? and denom = ? group by Hash",
                        wallet?.address, wallet?.address, if (Constant.main) "1" else "0",
                        wallet?.unit)
                        .order("Time desc")
                        .limit(pageSize * page)
                        .findAsync(TxItem::class.java).listen { list ->
                            setData(load, list)
                        }
            }
            1 -> {
                LitePal.where("From = ?  and isMain = ? and denom = ? group by Hash",
                        wallet?.address, if (Constant.main) "1" else "0",
                        wallet?.unit)
                        .order("Time desc")
                        .limit(pageSize * page)
                        .findAsync(TxItem::class.java).listen { list ->
                            setData(load, list)
                        }
            }
            2 -> {
                LitePal.where("To = ?  and isMain = ? and denom = ? group by Hash",
                        wallet?.address, if (Constant.main) "1" else "0",
                        wallet?.unit)
                        .order("Time desc")
                        .limit(pageSize * page)
                        .findAsync(TxItem::class.java).listen { list ->
                            setData(load, list)
                        }
            }
            else -> {
                LitePal.where("(From = ? or To = ?) and isMain = ? and denom = ? and type != ? group by Hash",
                        wallet?.address, wallet?.address, if (Constant.main) "1" else "0",
                        wallet?.unit, "0")
                        .order("Time desc")
                        .limit(pageSize * page)
                        .findAsync(TxItem::class.java).listen { list ->
                            setData(load, list)
                        }
            }
        }

    }

}