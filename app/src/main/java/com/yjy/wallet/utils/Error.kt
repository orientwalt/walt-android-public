package com.yjy.wallet.utils

import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.net.exception.BackErrorException
import com.weiyu.baselib.util.BLog
import org.web3j.crypto.CipherException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * weiweiyu
 * 2019/8/1
 * 575256725@qq.com
 * 13115284785
 * 错误信息
 */
class Error {
    companion object {
        fun error(e: Throwable?, context: BaseActivity) {
            BLog.d("-----------------------${e}")
            context.dismissProgressDialog()
            if (e is CipherException) {
                context.toast(context.resources.getString(com.yjy.wallet.R.string.send_err_pwd))
            } else if (e is HttpException) {     //   HTTP错误
                context.toast("Network connection error")
            } else if (e is ConnectException || e is UnknownHostException) {   //   连接错误
                context.toast("connection error")
            } else if (e is InterruptedIOException) {   //  连接超时
                context.toast("Connection timed out")
            } else if (e is BackErrorException) {
                context.toast(e.errMsg)
            } else {
                context.toast("unknown error $e")
            }
        }
    }
}
