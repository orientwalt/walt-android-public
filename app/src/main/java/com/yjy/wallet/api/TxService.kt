package com.yjy.wallet.api

import com.weiyu.baselib.net.ApiManager
import com.weiyu.baselib.net.BaseResult
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.bean.htdf.AccountInfo
import com.yjy.wallet.bean.htdf.HRCBean
import com.yjy.wallet.bean.htdftx.HtdfBalance
import com.yjy.wallet.bean.htdftx.Trade
import com.yjy.wallet.bean.htdftx.TradeRep
import com.yjy.wallet.wallet.WaltType
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

//节点切换不能用单例
class TxService(var type: String) {
    val htdf: String = if (main) "https://www.htdfscan.com/-/blockchain/htdf/server/" else "http://test-system.htdfscan.com"
    val usdp: String = if (main) "https://www.usdpscan.io/-/blockchain/usdp/server/" else "http://test-system.usdpscan.io"
    val htd: String = if (main) "https://www.hetbiscan.com/-/blockchain/het/server/" else "http://test-system.hetbiscan.io"
    fun txApi(): TxApi {
        var base = when (WaltType.valueOf(type)) {
            WaltType.htdf -> htdf
            WaltType.usdp -> usdp
            WaltType.HET -> htd
            else -> ""
        }
        return ApiManager.instance.getService(base, TxApi::class.java)
    }

    fun getTx(address: String, page: String, pageSize: Int): Flowable<TradeRep> = txApi().getTx(address, page, pageSize.toString(), 0)

    fun getTokenTx(contract: String, address: String, page: String, pageSize: Int): Flowable<TradeRep> = txApi().getTokenTx(contract, address, page, pageSize.toString(), 0)

    fun getHistory(address: String, height: String): Flowable<TradeRep> = txApi().getHistory(address, height, "yes").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun blocklog(address: String, page: String, type: String, to: String): Flowable<BaseResult<List<Trade>>> = txApi().blocklog(address, to, type, page, "50").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    //hrc-20
    fun accountInfo(address: String): Flowable<HtdfBalance> = txApi().accountInfo(address, "1").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun getAccount(address: String): Flowable<BaseResult<AccountInfo>> = txApi().getinfo(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun getBlockLatest(): Flowable<String> = txApi().getBlock().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun tokenBalance(address: String): Flowable<BaseResult<List<HRCBean>>> = txApi().balance(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun searchToken(address: String): Flowable<BaseResult<List<HRCBean>>> = txApi().gettokeninfo(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}