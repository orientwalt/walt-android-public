package com.yjy.wallet.activity.eth

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.fragment_info.*
import org.litepal.LitePal
import org.web3j.utils.Convert
import java.math.BigDecimal

/**
 * weiweiyu
 * 2020/7/16
 * 575256725@qq.com
 * 13115284785
 */
class ETHInfoFragment : LazyFragment() {
    var adapter: LQRAdapterForRecyclerView<TxItem>? = null
    var wallet: WInfo? = null
    var type = 0

    override fun getContentLayoutResId(): Int = R.layout.fragment_info

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun initializeContentViews() {
        EventBus.getDefault().register(this)
        wallet = arguments?.getSerializable("data") as WInfo
        type = arguments?.getInt("type") ?: 0
        adapter = object : LQRAdapterForRecyclerView<TxItem>(activity, arrayListOf(), R.layout.adapter_transations_item) {
            override fun convert(helper: LQRViewHolderForRecyclerView?, item: TxItem, position: Int) {
                val tvPrice = helper?.getView<AppCompatTextView>(R.id.tv_price)
                helper?.setText(R.id.tv_exchang_time, TimeUtils.stampToDate(item.Time))
                var icon = helper?.getView<ImageView>(R.id.iv_icon)
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
                    val intent = Intent(activity, ETHTxInfoActivity::class.java)
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
    @SuppressLint("CheckResult")
    fun getDataList(wallet: WInfo, load: Boolean) {
        when (wallet.unit) {
            WaltType.ETC.name, WaltType.ETH.name -> {
                TokenSendService().getATxs(wallet.address, page.toString(), WaltType.valueOf(wallet.unit), pageSize.toString())
                        .observeOn(Schedulers.io())
                        .compose(bindToLifecycle())
                        .map {
                            var list: MutableList<TxItem> = mutableListOf()
                            if (it.code == 1) {
                                it.data?.forEach {
                                    var txitem = TxItem(
                                            it.height.toString(),
                                            it.txid.toLowerCase(),
                                            it.from,
                                            it.to,
                                            it.value,
                                            wallet.unit,
                                            "",
                                            it.time * 1000,
                                            Constant.main
                                    )
                                    txitem.comfirm = it.confirmations
                                    txitem.type = it.toIsContract.toString()
                                    if (!TextUtils.isEmpty(it.traceErr)) {
                                        txitem.state = 2
                                    } else {
                                        if (it.confirmations > 0) {
                                            txitem.state = 1
                                        } else {
                                            txitem.state = 0
                                        }
                                    }

                                    txitem.fee = Convert.fromWei(it.fee, Convert.Unit.ETHER).toLong()
//                                    var lItem = LitePal.where("Hash = ? and denom = ?", it.txid.toLowerCase(), wallet.unit).findFirst(TxItem::class.java)
//                                    if (lItem == null || lItem.state != 1) {
                                    txitem.saveOrUpdate("Hash = ? and denom = ?", it.txid.toLowerCase(), wallet.unit)
//                                    }
                                    list.add(txitem)
                                }
                            }
                            it
                        }
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            getDb(load, type)
                            hideBar()
                            srl_coin.finishLoadmore()
                        }, {
                            getDb(load, type)
                            hideBar()
                            srl_coin.finishLoadmore()
                        })
            }
            else -> {
                TokenSendService().getTokenTxs(wallet.address, page.toString(), wallet.contract_address, WaltType.ETH, pageSize.toString())
                        .compose(bindToLifecycle())
                        .observeOn(Schedulers.io())
                        .map {
                            var list: MutableList<TxItem> = mutableListOf()
                            if (it.code == 1) {
                                it.data?.forEach {
                                    val tokenDecimal = BigDecimal.TEN.pow(it.tokenDecimals.toInt())
                                    val bigDecimal = BigDecimal(it.value).divide(tokenDecimal)
                                    var txitem = TxItem(
                                            it.block_no.toString(),
                                            it.txid.toLowerCase(),
                                            it.from,
                                            it.to,
                                            bigDecimal.toString(),
                                            wallet.unit,
                                            "",
                                            it.time * 1000,
                                            Constant.main
                                    )
                                    txitem.token = it.token
                                    txitem.comfirm = it.conformations
                                    txitem.state = 1
                                    list.add(txitem)
//                                    var lItem = LitePal.where("Hash = ? and token = ?", it.txid.toLowerCase(), wallet.contract_address).findFirst(TxItem::class.java)
//                                    if (lItem == null || lItem.state != 1) {
                                    txitem.saveOrUpdate("Hash = ? and token = ?", it.txid.toLowerCase(), wallet.contract_address)
//                                    }
                                }
                            }
                            it
                        }
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            getDb(load, type)
                            srl_coin.finishLoadmore()
                            hideBar()
                        }, {
                            getDb(load, type)
                            srl_coin.finishLoadmore()
                            hideBar()
                        })
            }
        }
    }

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
        if (wallet?.unit == WaltType.ETH.name || wallet?.unit == WaltType.ETC.name) {
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
                    LitePal.where("From = ?  and isMain = ? and denom = ? and type = ? group by Hash",
                            wallet?.address, if (Constant.main) "1" else "0",
                            wallet?.unit, "0")
                            .order("Time desc")
                            .limit(pageSize * page)
                            .findAsync(TxItem::class.java).listen { list ->
                                setData(load, list)
                            }
                }
                2 -> {
                    LitePal.where("To = ?  and isMain = ? and denom = ? and type = ? group by Hash",
                            wallet?.address, if (Constant.main) "1" else "0",
                            wallet?.unit, "0")
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
        } else {
            when (type) {
                0 -> {
                    LitePal.where("(From = ? or To = ?) and isMain = ? and token = ? group by Hash",
                            wallet?.address, wallet?.address, if (Constant.main) "1" else "0",
                            wallet?.contract_address)
                            .order("Time desc")
                            .limit(pageSize * page)
                            .findAsync(TxItem::class.java).listen { list ->
                                setData(load, list)
                            }
                }
                1 -> {
                    LitePal.where("From = ?  and isMain = ? and token = ? group by Hash",
                            wallet?.address, if (Constant.main) "1" else "0",
                            wallet?.contract_address)
                            .order("Time desc")
                            .limit(pageSize * page)
                            .findAsync(TxItem::class.java).listen { list ->
                                setData(load, list)
                            }
                }
                2 -> {
                    LitePal.where("To = ?  and isMain = ? and token = ? group by Hash",
                            wallet?.address, if (Constant.main) "1" else "0",
                            wallet?.contract_address)
                            .order("Time desc")
                            .limit(pageSize * page)
                            .findAsync(TxItem::class.java).listen { list ->
                                setData(load, list)
                            }
                }
                else -> {
                    LitePal.where("(From = ? or To = ?) and isMain = ? and token = ? and type != ? group by Hash",
                            wallet?.address, wallet?.address, if (Constant.main) "1" else "0",
                            wallet?.contract_address, "0")
                            .order("Time desc")
                            .limit(pageSize * page)
                            .findAsync(TxItem::class.java).listen { list ->
                                setData(load, list)
                            }
                }
            }
        }
    }

}