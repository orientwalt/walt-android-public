package com.weiyu.baselib.net

import android.text.TextUtils
import com.google.gson.JsonParseException
import com.weiyu.baselib.R
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.net.exception.BackErrorException
import com.weiyu.baselib.update.SysUtils
import com.weiyu.baselib.user.UserUtil
import de.greenrobot.event.EventBus
import io.reactivex.subscribers.ResourceSubscriber
import org.json.JSONException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.ParseException

abstract class DefaultObserver2<T>(activity: BaseActivity) : ResourceSubscriber<T>() {
    override fun onComplete() {
    }
    constructor(activity: BaseActivity, msg: String) : this(activity) {
        this.activity = activity
        activity.showProgressDialog(msg)
    }
    private var activity: BaseActivity = activity
    override fun onNext(t: T) {
        activity.dismissProgressDialog()
        onSuccess(t)

    }
    override fun onError(e: Throwable) {
        activity.dismissProgressDialog()
        if (SysUtils.getNetWorkState(activity) == -1) {
            onException(ExceptionReason.NOT_NET_ERROR)
        } else
            if (e is HttpException) {     //   HTTP错误
                onException(ExceptionReason.BAD_NETWORK)
            } else if (e is ConnectException || e is UnknownHostException) {   //   连接错误
                onException(ExceptionReason.CONNECT_ERROR)
            } else if (e is InterruptedIOException) {   //  连接超时
                onException(ExceptionReason.CONNECT_TIMEOUT)
            } else if (e is JsonParseException
                    || e is JSONException
                    || e is ParseException
            ) {   //  解析错误
                onException(ExceptionReason.PARSE_ERROR)
            } else if (e is BackErrorException) {
                activity.toast(e.errMsg)
            } else {
                onErr(e)
            }
    }

    open fun onErr(e: Throwable) {
        onException(ExceptionReason.UNKNOWN_ERROR)
    }

    /**
     * 请求成功
     *
     * @param response 服务器返回的数据
     */
    abstract fun onSuccess(response: T)

    /**
     * 请求异常
     *
     * @param reason
     */
    private fun onException(reason: ExceptionReason) {
        when (reason) {
            DefaultObserver2.ExceptionReason.CONNECT_ERROR -> activity.toast("connection error")
            DefaultObserver2.ExceptionReason.CONNECT_TIMEOUT -> activity.toast("Connection timed out")
            DefaultObserver2.ExceptionReason.BAD_NETWORK -> activity.toast("Network connection error")
            DefaultObserver2.ExceptionReason.PARSE_ERROR -> activity.toast("parse_error")
            DefaultObserver2.ExceptionReason.UNKNOWN_ERROR -> activity.toast("unknown_error")
            DefaultObserver2.ExceptionReason.NOT_NET_ERROR -> activity.toast(activity.resources.getString(R.string.not_net))
        }
    }

    /**
     * 请求网络失败原因
     */
    enum class ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
        NOT_NET_ERROR
    }
}