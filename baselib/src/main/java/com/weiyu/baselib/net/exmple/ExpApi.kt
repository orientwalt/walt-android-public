package com.weiyu.baselib.net.exmple

import io.reactivex.Flowable
import retrofit2.http.POST
import retrofit2.http.Query

interface ExpApi {
    @POST("/test")
    fun exmple(@Query("id") id: String): Flowable<String>
}
