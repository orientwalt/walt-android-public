package com.yjy.wallet.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.Constant
import com.yjy.wallet.bean.xrp.XRPBalance
import com.yjy.wallet.bean.xrp.XRPSend
import com.yjy.wallet.bean.xrp.XRPTx
import com.yjy.wallet.bean.xrp.XRPTxs
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * weiweiyu
 * 2019/11/11
 * 575256725@qq.com
 * 13115284785
 */
class XRPService private constructor() {
    companion object {
        //https://s.devnet.rippletest.net:51234
        private val postUrl = if (Constant.main) "https://s1.ripple.com:51234" else "https://s.altnet.rippletest.net:51234"

        val instance: XRPService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            XRPService()
        }

        fun xrpApi(): XRPApi {
            return ApiManager.instance.getService(postUrl, XRPApi::class.java)
        }
    }

    fun balance(address: String): Flowable<XRPBalance> {
        val array = JsonArray()
        val param = JsonObject()
        param.addProperty("account", address)
        param.addProperty("strict", true)
        param.addProperty("ledger_index", "current")
        param.addProperty("queue", true)
        array.add(param)
        val json = getMainJsonParam("account_info")
        json.add("params", array)
        return xrpApi().balance(json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun txs(address: String, ledger_index_max: Int): Flowable<XRPTxs> {
        val array = JsonArray()
        val param = JsonObject()
        param.addProperty("account", address)
        param.addProperty("binary", false)
        param.addProperty("forward", false)
        param.addProperty("ledger_index_max", -1)
        param.addProperty("ledger_index_min", ledger_index_max)
        param.addProperty("limit", 30)
        array.add(param)
        val json = getMainJsonParam("account_tx")
        json.add("params", array)
        return xrpApi().txs(json)
    }

    fun tx(tx: String): Flowable<XRPTx> {
        val array = JsonArray()
        val param = JsonObject()
        param.addProperty("transaction", tx)
        param.addProperty("binary", false)
        array.add(param)
        val json = getMainJsonParam("tx")
        json.add("params", array)
        return xrpApi().tx(json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun send(tx: String): Flowable<XRPSend> {
        val array = JsonArray()
        val param = JsonObject()
        param.addProperty("tx_blob", tx)
        array.add(param)
        val json = getMainJsonParam("submit")
        json.add("params", array)
        return xrpApi().send(json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    private fun getMainJsonParam(method: String): JsonObject {
        val json = JsonObject()
        json.addProperty("jsonrpc", "2.0")
        json.addProperty("method", method)
        json.addProperty("id", "rpccall")
        return json
    }
}
