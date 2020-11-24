package com.yjy.wallet.api

import com.google.gson.JsonObject
import com.yjy.wallet.bean.qtum.ItemsItem
import com.yjy.wallet.bean.qtum.QtumSend
import com.yjy.wallet.bean.qtum.QtumTxs
import com.yjy.wallet.bean.qtum.QtumUtxo
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * weiweiyu
 * 2019/12/17
 * 575256725@qq.com
 * 13115284785
 */
interface QtumApi {
    @GET("/insight-api/addr/{address}/balance")
    fun getBalance(@Path("address") address: String): Flowable<String>

    @GET("/insight-api/addr/{address}/utxo")
    fun getUTXOByAddress(@Path("address") address: String): Flowable<List<QtumUtxo>>

    @GET("/insight-api/addrs/{address}/txs?from=0&to=50")
    fun getTransactions(@Path("address") address: String): Flowable<QtumTxs>

    @GET("/insight-api/tx/{txid}")
    fun tx(@Path("txid") txid: String): Flowable<ItemsItem>

    @POST("/insight-api/tx/send")
    fun sendRawTx(@Body json: JsonObject): Flowable<QtumSend>

    @GET("/insight-api/blocks/?format=json")
    fun getCurrentBlockHeight(): Flowable<String>
    //    @GET("insight-api/addr/{address}/utxo")
//    fun getUTXOByAddressForToken(@Path("address") address: String): Call<List<ContractUnspentOutput>>

    //    @GET("/insight-api/tokens/{token}/addresses/{address}/balance")
//     fun getTokenBalance(@Path("token") token: String, @Path("address") address: String): Call<ResponseBody>

//    @GET("/insight-api/tokens/{token}/transactions")
//     fun getTokenTransactions(@Path("token") token: String, @Query("addresses") vararg addresses: String): Call<TransactionsInsightListResponse>
}