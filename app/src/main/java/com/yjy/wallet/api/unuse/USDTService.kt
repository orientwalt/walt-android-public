package com.yjy.wallet.api.unuse

import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.bean.usdt.USDTRsp
import com.yjy.wallet.bean.usdt.USDTTx
import com.yjy.wallet.bean.usdt.UsdtSend
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody


/**
 *Created by weiweiyu
 *on 2019/5/31
 * ETH钱包查询转账
 */
class USDTService {
    //    val base: String = if (Constant.main) "https://blockchain.info" else "https://testnet.blockchain.info"
    val base: String = "https://api.omniexplorer.info"

    fun usdtApi(): USDTApi {
        return ApiManager.instance.getService(base, USDTApi::class.java)
    }

    fun balance(addr: String): Flowable<USDTRsp> {
        var requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), addr)
        return usdtApi().balance(requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun txs(addr: String): Flowable<USDTRsp> {
        var requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), addr)
        return usdtApi().txs(requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun tx(hax: String): Flowable<USDTTx> {
        return usdtApi().tx(hax).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun send(hex: String): Flowable<UsdtSend> {
        return usdtApi().pushtx(hex).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

}