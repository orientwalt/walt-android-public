package com.yjy.wallet.api.unuse

import com.google.gson.JsonObject
import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.bean.bch.*
import com.yjy.wallet.wallet.WaltType
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 *Created by weiweiyu
 *on 2019/5/31
 * ETH钱包查询转账
 */
class BCHService(unit: String) {
    val base: String = when (unit) {
        WaltType.BCH.name -> "https://bch-chain.api.btc.com/v3/"
        WaltType.LTC.name -> "https://ltc-chain.api.btc.com/v3/"
        WaltType.BTC.name -> "https://chain.api.btc.com/v3/"
        else -> "https://chain.api.btc.com/v3/"
    }

    fun bchApi(): BCHApi {
        return ApiManager.instance.getService(base, BCHApi::class.java)
    }

    fun balance(address: String): Flowable<BchBalance> = bchApi().balance(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun tx(hax: String): Flowable<BchTx> = bchApi().tx(hax).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun txs(address: String): Flowable<BchTxs> = bchApi().txs(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun unspent(address: String, page: Int): Flowable<BchUnspent> = bchApi().unspent(address, page).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun send(hex: String): Flowable<BchSend> {
        val json = JsonObject()
        json.addProperty("rawhex", hex)
        return bchApi().send(json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun unspents(address: String): Flowable<BchUnspent> {
        return Flowable.range(1, 40)
                .concatMap {
                    unspent(address, it)
                }
                .takeWhile { it.data.page < it.data.page_total }
                .scan { t1: BchUnspent, t2: BchUnspent ->
                    t1.data.list.addAll(t2.data.list)
                    t1
                }

    }
}