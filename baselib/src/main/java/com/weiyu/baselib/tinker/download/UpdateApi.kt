package com.weiyu.baselib.tinker.download

import io.reactivex.Flowable
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UpdateApi {
    @POST("api/android/getdown")
    fun getVersion(@Query("name") id: String, @Header("browser") browser: String,@Query("version") version:String): Flowable<VersionUpdateBean>
}
