package com.weiyu.baselib.net

import android.text.TextUtils
import com.google.gson.JsonParseException
import com.weiyu.baselib.R
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.net.exception.BackErrorException
import com.weiyu.baselib.update.SysUtils
import com.weiyu.baselib.user.UserUtil
import com.weiyu.baselib.util.BLog
import de.greenrobot.event.EventBus
import io.reactivex.subscribers.ResourceSubscriber
import org.json.JSONException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.ParseException

abstract class DefaultObserver<T : BaseResult<*>>(activity: BaseActivity) : ResourceSubscriber<T>() {
    override fun onComplete() {
    }

    constructor(activity: BaseActivity, msg: String) : this(activity) {
        this.activity = activity
        activity.showProgressDialog(msg)
    }

    private var show = false
    private var activity: BaseActivity = activity
    //  Activity 是否在执行onStop()时取消订阅
    private val isAddInStop = false

//    @Override
//    public void onSubscribe(@NonNull Disposable d) {
//
//    }

    override fun onNext(t: T) {
        activity.dismissProgressDialog()
        if (t.code == 200 || t.err_code == 200) {
            onSuccess(t)
        } else {
            onFail(t)
        }
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
//    @Override
//    public void onComplete() {
//
//    }

    /**
     * 请求成功
     *
     * @param response 服务器返回的数据
     */
    abstract fun onSuccess(response: T)

    /**
     * 服务器返回数据，但响应码不为200
     *
     * @param response 服务器返回的数据
     */
    open fun onFail(response: T) {
        if (response.code == 10028) {
            val user = UserUtil.instance.getUser() ?: return
            user.isLogin = false
            EventBus.getDefault().post(user)
        }
        val message = ErrorMsg.getErrMsgByCode(activity, response.code)
        if (TextUtils.isEmpty(message)) {
            if (TextUtils.isEmpty(response.msg)) {
                activity.toast("unknown error")
            } else {
                activity.toast(response.msg!!)
            }
        } else {
            activity.toast(message)
        }
    }

    /**
     * 请求异常
     *
     * @param reason
     */
    private fun onException(reason: ExceptionReason) {
        when (reason) {
            DefaultObserver.ExceptionReason.CONNECT_ERROR -> activity.toast("connection error")
            DefaultObserver.ExceptionReason.CONNECT_TIMEOUT -> activity.toast("Connection timed out")
            DefaultObserver.ExceptionReason.BAD_NETWORK -> activity.toast("Network connection error")
            DefaultObserver.ExceptionReason.PARSE_ERROR -> activity.toast("parse_error")
            DefaultObserver.ExceptionReason.UNKNOWN_ERROR -> activity.toast("unknown_error")
            DefaultObserver.ExceptionReason.NOT_NET_ERROR -> activity.toast(activity.resources.getString(R.string.not_net))
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