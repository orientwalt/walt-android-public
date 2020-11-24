package com.yjy.wallet.api

import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.Constant.Companion.mainHETNet
import com.yjy.wallet.Constant.Companion.mainUSDPNet
import com.yjy.wallet.Constant.Companion.nodeHET
import com.yjy.wallet.Constant.Companion.testUsdpIp
import com.yjy.wallet.bean.htdf.AccountInfo
import com.yjy.wallet.bean.htdf.Send
import com.yjy.wallet.bean.htdf.Transactions
import com.yjy.wallet.bean.htdf.TxRep
import com.yjy.wallet.bean.params.BroadCast
import com.yjy.wallet.bean.params.TransationsParams
import com.yjy.wallet.wallet.WaltType
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class USDPService(var type: String) {
    val baseUsdp: String = "http://${if (main) mainUSDPNet else testUsdpIp}:1317"
    val baseHET: String = "http://${if (main) mainHETNet else nodeHET}:1317"
    fun htdfApi(): USDPApi {
        var base = when (type) {
            WaltType.usdp.name -> baseUsdp
            WaltType.HET.name -> baseHET
            else -> ""
        }
        return ApiManager.instance.getService(base, USDPApi::class.java)
    }

    fun broadcast(params: BroadCast): Flowable<Send> = htdfApi().broadcast(params).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun getAccount(address: String): Flowable<AccountInfo> = htdfApi().getaccounts(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun transactions(transationsParams: TransationsParams): Flowable<Transactions> = htdfApi().transactions(transationsParams).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun gettx(hax: String): Flowable<TxRep> = htdfApi().gettx(hax).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun getBlockLatest(): Flowable<String> = htdfApi().getBlockLatest().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}