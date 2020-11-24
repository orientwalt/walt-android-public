package com.yjy.wallet.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.weiyu.baselib.net.ApiManager
import com.weiyu.baselib.net.BaseResult
import com.yjy.wallet.bean.cxc.CTx
import com.yjy.wallet.bean.cxc.CTxInfo
import com.yjy.wallet.bean.cxc.CUtxo
import com.yjy.wallet.bean.cxc.CXCSendResult
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Credentials

class CXCService {

    //    val base: String = "http://118.190.245.246:7318"
    val base: String = "http://161.117.89.200:7318/"
    val base2: String = "http://cxc-walletserver.owtcoin.ltd"
    fun cxcApi(): CXCApi {
        return ApiManager.instance.getService(base, CXCApi::class.java)
    }

    fun cxcApi2(): CXCApi {
        return ApiManager.instance.getService(base2, CXCApi::class.java)
    }

    fun showdeal(tx: String): Flowable<CTxInfo> {
        val array = JsonArray()
        array.add(tx)
        array.add(true)
        val json = getMainJsonParam("showrawdeal")
        json.add("configs", array)
        return cxcApi().showdeal(json, getCredentials()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun sendrawdeal(tx: String): Flowable<CXCSendResult> {
        val array = JsonArray()
        array.add(tx)
        val json = getMainJsonParam("sendrawdeal")
        json.add("configs", array)
        return cxcApi().sendrawdeal(json, getCredentials()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getbalance(address: String): Flowable<BaseResult<String>> = cxcApi2().getbalance(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun getutxo(address: String): Flowable<BaseResult<List<CUtxo>>> = cxcApi2().getutxo(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun gettradelist(address: String, height: String, page: Int, pageSize: Int): Flowable<BaseResult<List<CTx>>> = cxcApi2().gettradelist(address, height, page.toString(), pageSize.toString())

    private fun getCredentials() = Credentials.basic("cxcsrpc", "7cuuKpkDrzNpFQ3LGLjfUMS6f9ZwCBfqjpYTuKiLxmQ6")

    private fun getMainJsonParam(method: String): JsonObject {
        val json = JsonObject()
        json.addProperty("jsonrpc", "2.0")
        json.addProperty("method", method)
        json.addProperty("id", "rpccall")
        return json
    }
}