package com.yjy.wallet.api.unuse

import com.google.gson.JsonObject
import com.yjy.wallet.bean.etc.*
import io.reactivex.Flowable
import retrofit2.http.*

interface ETCApi {
    @Headers("X-API-Key:ec25a2aeda0e51bcadadc29269563c01a640490c")
    @GET("/v1/bc/etc/mainnet/address/{addr}/transactions")
    fun getTxs(@Path("addr") addr: String, @Query("index") index: String, @Query("limit") limit: String): Flowable<ETCBase<List<ETCTxsItem>>>

    @Headers("X-API-Key:ec25a2aeda0e51bcadadc29269563c01a640490c")
    @GET("/v1/bc/etc/mainnet/address/{addr}")
    fun balance(@Path("addr") addr: String): Flowable<ETCBase<ETCBalance>>

    @Headers("X-API-Key:ec25a2aeda0e51bcadadc29269563c01a640490c")
    @GET("/v1/bc/etc/mainnet/txs/basic/hash/{tx}")
    fun tx(@Path("tx") tx: String): Flowable<ETCBase<ETCTx>>

    @Headers("X-API-Key:ec25a2aeda0e51bcadadc29269563c01a640490c")
    @GET("/v1/bc/etc/mainnet/txs/fee")
    fun fee(): Flowable<ETCBase<ETCFee>>

    @Headers("X-API-Key:ec25a2aeda0e51bcadadc29269563c01a640490c")
    @POST("/v1/bc/etc/mainnet/txs/push")
    fun push(@Body json: JsonObject): Flowable<ETCBase<ETCSend>>
}
