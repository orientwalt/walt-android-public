package com.yjy.wallet.api

import com.google.gson.JsonObject
import com.yjy.wallet.bean.trx.rpc.*
import io.reactivex.Flowable
import retrofit2.http.*

/**
 * weiweiyu
 * 2019/11/11
 * 575256725@qq.com
 * 13115284785
 */
interface TRXApi {

    @GET("/v1/accounts/{address}")
    fun balance(@Path("address") address: String): Flowable<RpcTRXBalance>

    @GET("/v1/accounts/{address}/transactions")
    fun txs(@Path("address") address: String, @Query("limit") limit: String, @Query("min_timestamp") min_timestamp: String): Flowable<RpcTRXTxs>

    @POST("/walletsolidity/gettransactioninfobyid")
    fun tx(@Body json: JsonObject): Flowable<RpcTRXTXInfo>

    @POST("/wallet/broadcasttransaction")
    fun send(@Body json: JsonObject): Flowable<RpcTRXSend>

    @GET("/wallet/getnowblock")
    fun getLastBlock(): Flowable<RpcTRXBlock>

}