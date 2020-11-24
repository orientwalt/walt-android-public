package com.yjy.wallet.api.unuse

import com.google.gson.JsonObject
import com.yjy.wallet.bean.bch.*
import io.reactivex.Flowable
import retrofit2.http.*

interface BCHApi {

    @GET("address/{address}")
    fun balance(@Path("address") address: String): Flowable<BchBalance>

    @GET("tx/{txhash}")
    fun tx(@Path("txhash") txhash: String): Flowable<BchTx>

    @GET("address/{address}/tx")
    fun txs(@Path("address") address: String): Flowable<BchTxs>

    @GET("address/{address}/unspent")//未话费零钱列表
    fun unspent(@Path("address") address: String, @Query("page") page: Int): Flowable<BchUnspent>

    @POST("tools/tx-publish/")
    fun send(@Body json: JsonObject): Flowable<BchSend>
}
