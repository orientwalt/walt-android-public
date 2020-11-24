package com.yjy.wallet.api

import com.google.gson.JsonObject
import com.yjy.wallet.bean.xrp.XRPBalance
import com.yjy.wallet.bean.xrp.XRPSend
import com.yjy.wallet.bean.xrp.XRPTx
import com.yjy.wallet.bean.xrp.XRPTxs
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * weiweiyu
 * 2019/11/11
 * 575256725@qq.com
 * 13115284785
 */
interface XRPApi {

    @POST("/")
    fun balance(@Body json: JsonObject): Flowable<XRPBalance>

    @POST("/")
    fun txs(@Body json: JsonObject): Flowable<XRPTxs>

    @POST("/")
    fun tx(@Body json: JsonObject): Flowable<XRPTx>

    @POST("/")
    fun send(@Body json: JsonObject): Flowable<XRPSend>
}