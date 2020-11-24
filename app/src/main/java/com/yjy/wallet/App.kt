package com.yjy.wallet

import com.tencent.bugly.crashreport.CrashReport
import com.weiyu.baselib.base.BaseApp
import org.bitcoinj.utils.BriefLogFormatter
import org.litepal.LitePal

/**
 *Created by weiweiyu
 * 575256725@qq.com
 *on 2019/7/9
 */
class App : BaseApp() {
    override fun onCreate() {
        super.onCreate()
        LitePal.initialize(this)
        CrashReport.initCrashReport(this, "ae50158086", false)
        BriefLogFormatter.init()
//        KConstant.init(this)
    }
}