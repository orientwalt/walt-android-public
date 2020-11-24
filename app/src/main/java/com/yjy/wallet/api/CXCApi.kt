package com.yjy.wallet.api

import com.google.gson.JsonObject
import com.weiyu.baselib.net.BaseResult
import com.yjy.wallet.bean.cxc.CTx
import com.yjy.wallet.bean.cxc.CTxInfo
import com.yjy.wallet.bean.cxc.CUtxo
import com.yjy.wallet.bean.cxc.CXCSendResult
import io.reactivex.Flowable
import retrofit2.http.*

interface CXCApi {

    @POST("/")
    fun showdeal(@Body json: JsonObject, @Header("Authorization") credentials: String): Flowable<CTxInfo>

    @POST("/")
    fun sendrawdeal(@Body json: JsonObject, @Header("Authorization") credentials: String): Flowable<CXCSendResult>

    @GET("/api/getlist")
    fun getutxo(@Query("address") address: String): Flowable<BaseResult<List<CUtxo>>>

    @GET("/api/getbalance")
    fun getbalance(@Query("address") address: String): Flowable<BaseResult<String>>

    @GET("/api/gettradelist")
    fun gettradelist(@Query("address") address: String, @Query("height") height: String, @Query("page") page: String, @Query("limit") limit: String): Flowable<BaseResult<List<CTx>>>
}
