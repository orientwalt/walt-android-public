package com.yjy.wallet.api.unuse

import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.bean.btc.BTCFee
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * weiweiyu
 * 2019/7/29
 * 575256725@qq.com
 * 13115284785
 */
class BtcFeeService() {

    fun btcApi(): BtcFeeApi {
        return ApiManager.instance.getService("https://bitcoinfees.earn.com", BtcFeeApi::class.java)
    }

    fun getfee(): Flowable<BTCFee> = btcApi().getFee().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

}