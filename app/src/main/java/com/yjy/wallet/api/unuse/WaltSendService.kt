package com.yjy.wallet.api.unuse

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.Constant
import com.yjy.wallet.wallet.WaltType
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

//节点切换不能用单例
class WaltSendService(var type: WaltType) {

    val htdf: String = if (Constant.main) "http://walletnode1.owtcoin.ltd:8500/" else "http://192.168.10.11/"
    fun sendApi(): WaltSendApi {
        var base = when (type) {
            WaltType.BTC, WaltType.USDT -> "btc"
            WaltType.BSV -> "bsv"
            WaltType.BCH -> "bch"
            else -> ""
        }
        return ApiManager.instance.getService(base, WaltSendApi::class.java)
    }

    fun send(tx: String): Flowable<String> {
        val array = JsonArray()
        array.add(tx)
        val json = getMainJsonParam("sendrawtransaction")
        json.add("configs", array)
        return sendApi().send(json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    private fun getMainJsonParam(method: String): JsonObject {
        val json = JsonObject()
        json.addProperty("jsonrpc", "2.0")
        json.addProperty("method", method)
        json.addProperty("id", "rpccall")
        return json
    }
}