package com.yjy.wallet.api.unuse

import com.google.gson.JsonObject
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * weiweiyu
 * 2019/12/18
 * 575256725@qq.com
 * 13115284785
 */
interface WaltSendApi {
    @POST("/")
    fun send(@Body json: JsonObject): Flowable<String>
}