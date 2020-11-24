package com.yjy.wallet.api.unuse

import com.google.gson.JsonObject
import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.bean.bsv.BSVBalance
import com.yjy.wallet.bean.bsv.BSVTx
import com.yjy.wallet.bean.bsv.BSVTxs
import com.yjy.wallet.bean.bsv.BSVUtxo
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 *Created by weiweiyu
 *on 2019/5/31
 * ETH钱包查询转账
 */
class BSVService {
    val base: String = "https://api.whatsonchain.com/v1/bsv/main/"

    val base2: String = "https://api.blockchair.com/bitcoin-sv/"


    fun bchApi2(): BSVApi {
        return ApiManager.instance.getService(base2, BSVApi::class.java)
    }
    fun bchApi(): BSVApi {
        return ApiManager.instance.getService(base, BSVApi::class.java)
    }

    fun send(hax: String): Flowable<Any> {
        val json = JsonObject()
        json.addProperty("txhex", hax)
        return bchApi().send(json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
    fun tx2(hax: String): Flowable<String> = bchApi2().tx2(hax).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun balance(address: String): Flowable<BSVBalance> = bchApi().balance(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun txs(address: String): Flowable<List<BSVTxs>> = bchApi().history(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun tx(hax: String): Flowable<BSVTx> = bchApi().tx(hax).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun utxo(address: String): Flowable<List<BSVUtxo>> = bchApi().utxo(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}