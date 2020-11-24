package com.weiyu.baselib.net.exmple

import com.weiyu.baselib.net.ApiManager
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ExpService private constructor() {
    //单例模式不会刷新retrofit
    companion object {
        val base: String = ""
        val instance: ExpService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ExpService()
        }

        fun expApi(): ExpApi {
            return ApiManager.instance.getService(base, ExpApi::class.java)
        }

        fun exmple(): Flowable<String> =
            expApi().exmple("").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

}