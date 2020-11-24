package com.yjy.wallet.api

import com.google.gson.JsonObject
import com.yjy.wallet.bean.neo.NEOBalance
import com.yjy.wallet.bean.neo.NEOTxInfo
import com.yjy.wallet.bean.neo.NEOTxs
import com.yjy.wallet.bean.neo.NeoSend
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * weiweiyu
 * 2019/11/11
 * 575256725@qq.com
 * 13115284785
 */
interface NEOApi {

    @GET("get_balance/{address}")
    fun balance(@Path("address") address: String): Flowable<NEOBalance>

    @GET("get_address_abstracts/{address}/{page}")
    fun txs(@Path("address") address: String, @Path("page") page: String): Flowable<NEOTxs>

    @GET("get_transaction/{hex}")
    fun tx(@Path("hex") hex: String): Flowable<NEOTxInfo>

    @POST("/api/{path}")
    fun send(@Body json: JsonObject, @Path("path") net: String): Flowable<NeoSend>

}