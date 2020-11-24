package com.yjy.wallet.api.unuse

import com.yjy.wallet.bean.btc.BTCFee
import io.reactivex.Flowable
import retrofit2.http.GET

/**
 * weiweiyu
 * 2019/7/29
 * 575256725@qq.com
 * 13115284785
 * BTC查询
 * https://developer.bitaps.com/blockchain#API_endpoint
 */

interface BtcFeeApi {

    @GET("/api/v1/fees/recommended")
    fun getFee(): Flowable<BTCFee>

}