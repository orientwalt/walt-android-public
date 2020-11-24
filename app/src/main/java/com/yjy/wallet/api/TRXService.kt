package com.yjy.wallet.api

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.Constant
import com.yjy.wallet.bean.trx.rpc.*
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * weiweiyu
 * 2019/11/11
 * 575256725@qq.com
 * 13115284785
 */
class TRXService private constructor() {
    companion object {
        private val postUrl = if (Constant.main) "https://api.trongrid.io" else "https://api.shasta.trongrid.io"

        val instance: TRXService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TRXService()
        }

        fun xrpApi(): TRXApi {
            return ApiManager.instance.getService(postUrl, TRXApi::class.java)
        }
    }

    fun balance(address: String): Flowable<RpcTRXBalance> {
        return xrpApi().balance(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun txs(address: String, mintime: Long): Flowable<RpcTRXTxs> {
        return xrpApi().txs(address, "30", mintime.toString())
    }

    fun tx(tx: String): Flowable<RpcTRXTXInfo> {
        val json = JsonObject()
        json.addProperty("value", tx)
        return xrpApi().tx(json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun send(tx: String): Flowable<RpcTRXSend> {
       var param=Gson().fromJson<JsonObject>(tx,JsonObject::class.java)
        return xrpApi().send(param).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getLastBlock(): Flowable<RpcTRXBlock> {
        return xrpApi().getLastBlock().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

}
