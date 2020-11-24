package com.yjy.wallet.api.unuse

import com.yjy.wallet.bean.usdt.USDTRsp
import com.yjy.wallet.bean.usdt.USDTTx
import com.yjy.wallet.bean.usdt.UsdtSend
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * weiweiyu
 * 2019/7/30
 * 575256725@qq.com
 * 13115284785
 */
interface USDTApi {
    @Multipart
    @POST("/v1/address/addr/")
    fun balance(@Part("addr") addr: RequestBody): Flowable<USDTRsp>

    @Multipart
    @POST("/v1/address/addr/details/")
    fun txs(@Part("addr") addr: RequestBody): Flowable<USDTRsp>

    @GET("/v1/transaction/tx/{hash}")
    fun tx(@Path("hash") hash: String): Flowable<USDTTx>

    @FormUrlEncoded
    @POST("/v1/transaction/pushtx/")
    fun pushtx(@Field("signedTransaction") tx: String): Flowable<UsdtSend>
}