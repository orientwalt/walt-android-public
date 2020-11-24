package com.weiyu.baselib.base

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Environment
import android.support.multidex.MultiDex
import com.lzy.okgo.OkGo
import com.lzy.okserver.OkDownload
import com.weiyu.baselib.tinker.util.BaseApplicationContext
import com.weiyu.baselib.util.LanguageUtil

/**
 *Created by weiweiyu
 * 575256725@qq.com
 *on 2019/7/9
 */

open class BaseApp:Application(){
    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
        languageWork()
        OkGo.getInstance().init(this)
        OkDownload.getInstance().folder = Environment.getExternalStorageDirectory().absolutePath + "/wallet_tinker"
        BaseApplicationContext.application = this
        BaseApplicationContext.context = this
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        languageWork()
    }

    private fun languageWork() {
        val locale = LanguageUtil.getLocale(this)
        LanguageUtil.updateLocale(this, locale)
    }
}