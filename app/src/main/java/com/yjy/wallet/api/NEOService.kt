package com.yjy.wallet.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.bean.neo.NEOBalance
import com.yjy.wallet.bean.neo.NEOTxInfo
import com.yjy.wallet.bean.neo.NEOTxs
import com.yjy.wallet.bean.neo.NeoSend
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * weiweiyu
 * 2019/11/11
 * 575256725@qq.com
 * 13115284785
 */
class NEOService private constructor() {
    companion object {
        private val send = "https://api.nel.group"
        private val postUrl = if (main) "https://neoscan.io/api/main_net/v1/" else "https://neoscan-testnet.io/api/test_net/v1/"
        val instance: NEOService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NEOService()
        }

        fun xrpApi(): NEOApi {
            return ApiManager.instance.getService(postUrl, NEOApi::class.java)
        }

        fun xrpApi2(): NEOApi {
            return ApiManager.instance.getService(send, NEOApi::class.java)
        }
    }

    fun balance(address: String): Flowable<NEOBalance> {
        return xrpApi().balance(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun txs(address: String,page:Int): Flowable<NEOTxs> {
        return xrpApi().txs(address, page.toString())
    }

    fun tx(tx: String): Flowable<NEOTxInfo> {
        return xrpApi().tx(tx).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun send(tx: String): Flowable<NeoSend> {
        val array = JsonArray()
        array.add(tx)
        var json = JsonObject()
        json.addProperty("jsonrpc", "2.0")
        json.addProperty("id", 1)
        json.addProperty("method", "sendrawtransaction")
        json.add("params", array)
        return xrpApi2().send(json, if (main) "mainnet" else "testnet").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}
