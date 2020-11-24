package com.weiyu.baselib.tinker.download

import com.weiyu.baselib.net.ApiManager
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UpdateService private constructor() {
    //单例模式不会刷新retrofit
    companion object {
//                val base: String = "http://uat-edition.eqlvv.com:8083/"
        val base: String = "https://pro-edition.eqlvv.com:8083/"
        val instance: UpdateService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            UpdateService()
        }

        fun updateApi(): UpdateApi {
            return ApiManager.instance.getService(base, UpdateApi::class.java)
        }


    }

    fun getVersion(name: String, version: String): Flowable<VersionUpdateBean> = updateApi().getVersion(name, "yjyzuiqiangioszuiqiangandroid", version).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}