package com.yjy.wallet.api

import com.google.gson.JsonObject
import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.Constant
import com.yjy.wallet.bean.qtum.ItemsItem
import com.yjy.wallet.bean.qtum.QtumSend
import com.yjy.wallet.bean.qtum.QtumTxs
import com.yjy.wallet.bean.qtum.QtumUtxo
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * weiweiyu
 * 2019/11/11
 * 575256725@qq.com
 * 13115284785
 */
class QtumService private constructor() {
    companion object {
        private val postUrl = if (Constant.main) "https://explorer.qtum.org/" else "https://testnet.qtum.org/"

        val instance: QtumService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            QtumService()
        }

        fun qtumApi(): QtumApi {
            return ApiManager.instance.getService(postUrl, QtumApi::class.java)
        }
    }

    fun balance(address: String): Flowable<String> {
        return qtumApi().getBalance(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun utxo(address: String): Flowable<List<QtumUtxo>> {
        return qtumApi().getUTXOByAddress(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun txs(address: String): Flowable<QtumTxs> {
        return qtumApi().getTransactions(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun tx(address: String): Flowable<ItemsItem> {
        return qtumApi().tx(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun send(tx: String): Flowable<QtumSend> {
        var json = JsonObject()
        json.addProperty("rawtx", tx)
        return qtumApi().sendRawTx(json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}
