package com.yjy.wallet.api

import com.google.gson.JsonObject
import com.yjy.wallet.bean.eos.*
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface EOSApi {

    @POST("/v1/chain/get_info")
    fun get_info(@Header("apikey") apikey: String): Flowable<EosInfo>

    @POST("/v1/chain/get_block")
    fun get_block(@Header("apikey") apikey: String, @Body json: JsonObject): Flowable<EOSBlock>

    @POST("/v1/history/get_key_accounts")
    fun get_key_accounts(@Header("apikey") apikey: String, @Body json: JsonObject): Flowable<EosAccounts>

    @POST("/v1/third/get_transaction")
    fun get_transaction(@Header("apikey") apikey: String, @Body json: JsonObject): Flowable<EosTx>

    @POST("/v1/chain/get_account")
    fun get_account(@Header("apikey") apikey: String, @Body json: JsonObject): Flowable<EosAccount>

    @POST("/v1/chain/get_currency_balance")
    fun get_currency_balance(@Header("apikey") apikey: String, @Body json: JsonObject): Flowable<List<String>>

    @POST("/v2/third/get_account_transfer")
    fun get_account_transfer(@Header("apikey") apikey: String, @Body json: JsonObject): Flowable<EosTxs>

    @POST("/v1/chain/push_transaction")
    fun push_transaction(@Header("apikey") apikey: String, @Body json: String): Flowable<EosSend>
}
