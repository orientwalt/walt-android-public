package com.yjy.wallet.activity.htdf

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
import com.yjy.wallet.api.TxService
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
import java.math.BigDecimal

/**
 * weiweiyu
 * 2020/7/16
 * 575256725@qq.com
 * 13115284785
 */
class InfoFragment : LazyFragment() {
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
                var icon = helper?.getView<ImageView>(R.id.iv_icon)
                if (item.type == "1") {
                    icon?.visibility = View.INVISIBLE
                    helper?.setText(R.id.tv_address, resources.getString(R.string.htdf_contract_create))
                    helper?.setText(R.id.tv_price, "")
                } else {
                    icon?.visibility = View.VISIBLE
                    var p = Utils.toSubStringDegistForChart(
                            if (TextUtils.isEmpty(item.amount)) 0.0 else item.amount!!.replace(",", "").toDouble(),
                            Constant.priceP,
                            false
                    )
                    if (item.To == wallet?.address) {
                        helper?.setText(R.id.tv_address, item.From)
                        helper?.setText(
                                R.id.tv_price,
                                "+$p ${item.denom?.toUpperCase()}"
                        )
                        tvPrice?.setTextColor(resources.getColor(R.color.btn_bg_5f8def))
                        icon?.setImageDrawable(resources.getDrawable(R.mipmap.transfer_in))
                    } else {
                        icon?.setImageDrawable(resources.getDrawable(R.mipmap.transfer_out))
                        helper?.setText(R.id.tv_address, item.To)
                        helper?.setText(
                                R.id.tv_price,
                                "-$p ${item.denom?.toUpperCase()}"
                        )
                        tvPrice?.setTextColor(resources.getColor(R.color.txt_ff0000))
                    }
                }
                helper?.setOnItemClickListener { _, _, _, _ ->
                    val intent = Intent(activity, HtdfTxInfoActivity::class.java)
                    intent.putExtra("data", item)
                    intent.putExtra("walt", wallet)
                    if (item.To == wallet!!.address) {
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


    override fun lazyLoad() {
        getDb(false, type)
    }


    var page = 1
    var pageSize = 10
    @SuppressLint("CheckResult")
    fun getDataList(wallet: WInfo, load: Boolean) {
        var f = if (wallet.unit == WaltType.htdf.name) {
            TxService(WaltType.htdf.name).getTx(wallet.address, page.toString(), pageSize)
        } else
            TxService(WaltType.htdf.name).getTokenTx(wallet.contract_address, wallet.address, page.toString(), pageSize)
        f.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map {
                    var list: MutableList<TxItem> = mutableListOf()
                    for (i in it.trade.indices) {
                        var item = it.trade[i]
                        var money = if (wallet.unit == WaltType.htdf.name) item.money else {
                            val tokenDecimal = BigDecimal.TEN.pow(wallet.decimal)
                            val bigDecimal = BigDecimal(item.contract_money).divide(tokenDecimal)
                            bigDecimal.toString()
                        }
                        if (TextUtils.isEmpty(money)) {
                            money = "0"
                        }
                        var txItem = TxItem(
                                item.blockheight.toString(),
                                item.tradehash.toLowerCase(),
                                if (item.contract_type == "5") item.to else item.from,
                                if (item.contract_type == "5") item.from else item.to,
                                money,
                                wallet.unit,
                                item.memo,
                                TimeUtils.dateToStamp(item.tradetime),
                                Constant.main
                        )
                        if (wallet.unit != WaltType.htdf.name) {
                            txItem.token = item.contract_address
                        }
                        txItem.type = item.contract_type
                        txItem.state = when (item.inval) {
                            1 -> 1
                            0 -> 2
                            else -> 0
                        }
                        list.add(txItem)
                        if (wallet.unit != WaltType.htdf.name) {
//                            var lItem = LitePal.where("Hash = ? and token = ?", item.tradehash.toLowerCase(), wallet.contract_address).findFirst(TxItem::class.java)
//                            if (lItem == null || lItem.state != 1) {
                            txItem.saveOrUpdate("Hash = ? and token = ?", item.tradehash.toLowerCase(), wallet.contract_address)
//                            }
                        } else {
//                            var lItem = LitePal.where("Hash = ? and denom = ?", item.tradehash.toLowerCase(), wallet.unit).findFirst(TxItem::class.java)
//                            if (lItem == null || lItem.state != 1) {
                            txItem.saveOrUpdate("Hash = ? and denom = ?", item.tradehash.toLowerCase(), wallet.unit)
//                            }
                        }

                    }
//                    LitePal.saveAll(list)
                    list
                }
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
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
        if (wallet?.unit == WaltType.htdf.name) {
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