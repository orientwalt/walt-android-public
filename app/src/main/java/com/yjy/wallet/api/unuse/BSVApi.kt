package com.yjy.wallet.api.unuse

import com.google.gson.JsonObject
import com.yjy.wallet.bean.bsv.BSVBalance
import com.yjy.wallet.bean.bsv.BSVTx
import com.yjy.wallet.bean.bsv.BSVTxs
import com.yjy.wallet.bean.bsv.BSVUtxo
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BSVApi {

    @POST("address/{address}/balance")
    fun balance(@Path("address") address: String): Flowable<BSVBalance>

    @GET("tx/hash/{txhash}")
    fun tx(@Path("txhash") txhash: String): Flowable<BSVTx>

    @POST("address/{address}/history")
    fun history(@Path("address") address: String): Flowable<List<BSVTxs>>

    @GET("address/{address}/unspent")
    fun utxo(@Path("address") address: String): Flowable<List<BSVUtxo>>

    @POST("tx/raw")//{"txhex": "hex..."}
    fun send(@Body json: JsonObject): Flowable<Any>

    @GET("dashboards/transaction/{txhash}")
    fun tx2(@Path("txhash") txhash: String): Flowable<String>
}
