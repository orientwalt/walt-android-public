package com.weiyu.baselib.net

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiManager private constructor() {
    //设置连接超时的值
    private val TIMEOUT = 30

    companion object {
        val instance: ApiManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ApiManager()
        }
    }

    private fun getRetrofit(baseUrl: String, builder: OkHttpClient.Builder): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                //将client与retrofit关联
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(CustomGsonConverterFactory.create())
                .build()
    }

    fun <T> getService(baseUrl: String, service: Class<T>): T {

        var clien = OkHttp.builder

        val retrofit = getRetrofit(baseUrl, clien)
        return retrofit.create(service)
    }


}