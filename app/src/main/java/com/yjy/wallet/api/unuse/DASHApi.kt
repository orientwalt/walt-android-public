package com.yjy.wallet.api.unuse

import com.yjy.wallet.bean.dash.DashBalance
import com.yjy.wallet.bean.dash.DashTx
import com.yjy.wallet.bean.dash.DashTxs
import io.reactivex.Flowable
import org.json.JSONObject
import retrofit2.http.*

/**
 * weiweiyu
 * 2019/8/13
 * 575256725@qq.com
 * 13115284785
 */

interface DASHApi {
    @GET("/insight-api/addr/{Address}/balance")
    fun balance(@Path("Address") address: String): Flowable<Long>

    @GET("/insight-api/tx/{hax}")
    fun tx(@Path("hax") hax: String): Flowable<DashTx>

    @GET("/insight-api/txs")
    fun txs(@Query("address") address: String): Flowable<DashTxs>

    @GET("/insight-api/addr/{Address}/utxo")
    fun utxo(@Path("Address") address: String): Flowable<List<DashBalance>>

    @POST("txs/push")//{"tx":$TXHEX}
    fun push(@Body json: JSONObject, @Query("token") token: String)
}