package com.yjy.wallet.activity.other

import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.common.io.BaseEncoding
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.weiyu.baselib.base.LazyFragment
import com.weiyu.baselib.util.Utils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.api.*
import com.yjy.wallet.bean.LoadEvent
import com.yjy.wallet.bean.StopEvent
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.utils.TimeUtils
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import de.greenrobot.event.EventBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.fragment_info.*
import org.bitcoinj.core.Base58
import org.litepal.LitePal
import org.stellar.sdk.KeyPair
import org.stellar.sdk.xdr.PublicKey
import org.stellar.sdk.xdr.TransactionEnvelope
import org.stellar.sdk.xdr.XdrDataInputStream
import org.web3j.utils.Numeric
import java.io.ByteArrayInputStream
import java.math.BigDecimal

/**
 * weiweiyu
 * 2020/7/16
 * 575256725@qq.com
 * 13115284785
 */
class OtherFragment : LazyFragment() {
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
                    helper?.setText(R.id.tv_address, item.From)
                    helper?.setText(
                            R.id.tv_price,
                            "+${Utils.toSubStringDegistForChart(
                                    item.amount!!.toDouble(),
                                    Constant.priceP,
                                    false
                            )} ${item.denom?.toUpperCase()}"
                    )
                    tvPrice?.setTextColor(resources.getColor(R.color.btn_bg_5f8def))
                } else {
                    icon?.setImageDrawable(resources.getDrawable(R.mipmap.transfer_out))
                    helper?.setText(R.id.tv_address, item.To)
                    helper?.setText(
                            R.id.tv_price,
                            "-${Utils.toSubStringDegistForChart(
                                    item.amount!!.toDouble(),
                                    Constant.priceP,
                                    false
                            )} ${item.denom?.toUpperCase()}"
                    )
                    tvPrice?.setTextColor(resources.getColor(R.color.txt_ff0000))
                }
                helper?.setOnItemClickListener { _, _, _, _ ->
                    val intent = Intent(activity, TxInfoActivity::class.java)
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
        val waltType = WaltType.valueOf(wallet.unit)
        var s = object : ResourceSubscriber<MutableList<TxItem>>() {
            override fun onComplete() {
            }

            override fun onNext(it: MutableList<TxItem>) {
                hideBar()
                srl_coin.finishLoadmore()
                getDb(load, type)
            }

            override fun onError(t: Throwable?) {
                hideBar()
                srl_coin.finishLoadmore()
            }
        }
        when (waltType) {
            WaltType.XLM -> {
                XLMService().getTxs(wallet.address)
                        .observeOn(Schedulers.io())
                        .compose(bindToLifecycle())
                        .map {
                            it.records.forEach {
                                val bytes: ByteArray = BaseEncoding.base64().decode(it.envelopeXdr)
                                var t = TransactionEnvelope.decode(XdrDataInputStream(ByteArrayInputStream(bytes)))
                                var info = t.v1.tx.operations[0].body.createAccountOp
                                var publicKey = PublicKey()
                                publicKey.ed25519 = info.destination.accountID.ed25519
                                val tokenDecimal = BigDecimal.TEN.pow(7)
                                val bigDecimal = BigDecimal(info.startingBalance.int64).divide(tokenDecimal)
                                var txItem = TxItem(
                                        it.ledger.toString(),
                                        it.hash.toLowerCase(),
                                        it.sourceAccount,
                                        KeyPair.fromXdrPublicKey(publicKey).accountId,
                                        bigDecimal.toString(),
                                        waltType.name,
                                        it.memo.toString(),
                                        TimeUtils.strToTime(it.createdAt),
                                        Constant.main
                                )
                                txItem.fee = it.feeCharged
                                txItem.state = if (it.isSuccessful) 1 else 2
//                                var lItem = LitePal.where("Hash = ? and denom = ?", it.hash.toLowerCase(), wallet.unit).findFirst(TxItem::class.java)
//                                if (lItem == null || lItem.state != 1) {
                                txItem.saveOrUpdate("Hash = ? and denom = ?", it.hash.toLowerCase(), wallet.unit)
//                                }


                            }
                            var list: MutableList<TxItem> = mutableListOf()
                            list
                        }
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s)
            }
            WaltType.XRP -> {
                XRPService.instance.txs(wallet.address, 0)
                        .observeOn(Schedulers.io())
                        .compose(bindToLifecycle())
                        .map {
                            var list: MutableList<TxItem> = mutableListOf()
                            if (it.result.status == "success") {
                                it.result.transactions?.forEach { t ->
                                    val item = t.tx
                                    var txItem = TxItem(
                                            item.ledger_index.toString(),
                                            item.hash.toLowerCase(),
                                            item.Account,
                                            item.Destination,
                                            (item.Amount.toDouble() / 1000000).toString(),
                                            waltType.name,
                                            "",
                                            (item.date + 946684800) * 1000,
                                            Constant.main
                                    )
                                    txItem.fee = item.Fee.toLong()
                                    txItem.state = if (t.validated) 1 else 2
//                                    var lItem = LitePal.where("Hash = ? and denom = ?", item.hash.toLowerCase(), wallet.unit).findFirst(TxItem::class.java)
//                                    if (lItem == null || lItem.state != 1) {
                                    txItem.saveOrUpdate("Hash = ? and denom = ?", item.hash.toLowerCase(), wallet.unit)
//                                    }
                                }
                            }
                            list
                        }
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s)
            }
            WaltType.NEO -> {
                NEOService.instance.txs(wallet.address, page)
                        .observeOn(Schedulers.io())
                        .compose(bindToLifecycle())
                        .map {
                            var list: MutableList<TxItem> = mutableListOf()
                            it.entries?.forEach { item ->
                                if (item.asset == wallet.contract_address) {
                                    var txItem = TxItem(
                                            item.block_height.toString(),
                                            item.txid.toLowerCase(),
                                            item.address_from,
                                            item.address_to,
                                            item.amount,
                                            waltType.name,
                                            "",
                                            item.time * 1000,
                                            Constant.main)
                                    txItem.state = 1
//                                    var lItem = LitePal.where("Hash = ? and denom = ?", item.txid.toLowerCase(), wallet.unit).findFirst(TxItem::class.java)
//                                    if (lItem == null || lItem.state != 1) {
                                    txItem.saveOrUpdate("Hash = ? and denom = ?", item.txid.toLowerCase(), wallet.unit)
//                                    }

                                }
                            }
                            list
                        }
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s)
            }
            WaltType.TRX -> {
                TRXService.instance.txs(wallet.address, 0)
                        .observeOn(Schedulers.io())
                        .compose(bindToLifecycle())
                        .map {
                            var list: MutableList<TxItem> = mutableListOf()
                            if (it.success && it.data.isNotEmpty()) {
                                it.data.forEach { item ->
                                    var data = item.raw_data
                                    if (data.contract.isNotEmpty()) {
                                        if (data.contract[0].type == "TransferContract") {
                                            var value = data.contract[0].parameter.value
                                            var from = Base58.encodeChecked(0x41, Numeric.hexStringToByteArray(value.owner_address.substring(2)))
                                            var to = Base58.encodeChecked(0x41, Numeric.hexStringToByteArray(value.to_address.substring(2)))
                                            var txItem = TxItem(
                                                    "",
                                                    item.txID.toLowerCase(),
                                                    from,
                                                    to,
                                                    (value.amount.toDouble() / 1000000).toString(),
                                                    waltType.name,
                                                    "",
                                                    data.timestamp,
                                                    Constant.main
                                            )
                                            wallet.hight = data.timestamp
                                            if (item.ret.isNotEmpty()) {
                                                var ret = item.ret[0]
                                                txItem.state = if (ret.code == "SUCESS") 1 else 2
                                            }
//                                            var lItem = LitePal.where("Hash = ? and denom = ?", item.txID.toLowerCase(), wallet.unit).findFirst(TxItem::class.java)
//                                            if (lItem == null || lItem.state != 1) {
                                            txItem.saveOrUpdate("Hash = ? and denom = ?", item.txID.toLowerCase(), wallet.unit)
//                                            }
                                        }
                                    }
                                }
                            }
                            list
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s)
            }
            WaltType.usdp, WaltType.HET -> {
                TxService(wallet.unit).getTx(wallet.address, page.toString(), pageSize)
                        .observeOn(Schedulers.io())
                        .compose(bindToLifecycle())
                        .map {
                            var list: MutableList<TxItem> = mutableListOf()
                            for (i in it.trade.indices) {
                                var item = it.trade[i]
                                var txItem = TxItem(
                                        item.blockheight.toString(),
                                        item.tradehash.toLowerCase(),
                                        item.from,
                                        item.to,
                                        item.money,
                                        waltType.name,
                                        item.memo,
                                        TimeUtils.dateToStamp(item.tradetime),
                                        Constant.main
                                )
                                txItem.state = if (item.inval == 1) 1 else 2
                                if (i == 0) {
                                    wallet.hight = item.blockheight
                                    MyWalletUtils.instance.updateCheckInfo(wallet)
                                }
//                                var lItem = LitePal.where("Hash = ? and denom = ?", item.tradehash.toLowerCase(), wallet.unit).findFirst(TxItem::class.java)
//                                if (lItem == null || lItem.state != 1) {
                                txItem.saveOrUpdate("Hash = ? and denom = ?", item.tradehash.toLowerCase(), wallet.unit)
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