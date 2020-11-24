package com.weiyu.baselib.base

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.weiyu.baselib.R

class DialogHelper constructor(activity: Activity) {
    private var mActivity: Activity? = null
    private var mAlertDialog: AlertDialog? = null
    private var mToast: Toast? = null

    init {
        mActivity = activity
    }

    /**
     * 弹对话框
     *
     * @param title            标题
     * @param msg              消息
     * @param positive         确定
     * @param positiveListener 确定回调
     * @param negative         否定
     * @param negativeListener 否定回调
     */
    fun alert(
            title: String, msg: String, positive: String,
            positiveListener: DialogInterface.OnClickListener,
            negative: String, negativeListener: DialogInterface.OnClickListener
    ) {
        alert(title, msg, positive, positiveListener, negative, negativeListener, false)
    }

    /**
     * 弹对话框
     *
     * @param title                    标题
     * @param msg                      消息
     * @param positive                 确定
     * @param positiveListener         确定回调
     * @param negative                 否定
     * @param negativeListener         否定回调
     * @param isCanceledOnTouchOutside 是否可以点击外围框
     */
    fun alert(
            title: String?, msg: String?, positive: String?,
            positiveListener: DialogInterface.OnClickListener,
            negative: String?,
            negativeListener: DialogInterface.OnClickListener,
            isCanceledOnTouchOutside: Boolean?
    ) {
        dismissProgressDialog()
        mActivity!!.runOnUiThread(Runnable {
            if (mActivity!!.isFinishing) {
                return@Runnable
            }
            val builder = AlertDialog.Builder(mActivity)
            if (title != null) {
                builder.setTitle(title)
            }
            if (msg != null) {
                builder.setMessage(msg)
            }
            if (positive != null) {
                builder.setPositiveButton(positive, positiveListener)
            }
            if (negative != null) {
                builder.setNegativeButton(negative, negativeListener)
            }
            mAlertDialog!!.setCanceledOnTouchOutside(isCanceledOnTouchOutside!!)
            mAlertDialog!!.setCancelable(false)
            mAlertDialog = builder.show()

        })
    }

    /**
     * TOAST
     *
     * @param msg    消息
     * @param period 时长
     */
    fun toast(msg: String, period: Int) {
        mActivity?.runOnUiThread {
            if (mToast == null) {
                mToast = Toast(mActivity)
            }
            val view = LayoutInflater.from(mActivity).inflate(R.layout.view_transient_notification, null)
            val tv = view.findViewById(android.R.id.message) as TextView
            tv.text = msg
            mToast!!.view = view
            mToast!!.duration = period
            mToast!!.setGravity(Gravity.CENTER, 0, 0)
            mToast!!.show()
        }
    }

    fun toast(msg: String, period: Int, position: Int) {
        mActivity?.runOnUiThread {
            if (mToast == null) {
                mToast = Toast(mActivity)
            }
            val view = LayoutInflater.from(mActivity).inflate(R.layout.view_transient_notification, null)
            val tv = view.findViewById(android.R.id.message) as TextView
            tv.text = msg
            mToast!!.view = view
            mToast!!.duration = period
            mToast!!.setGravity(position, 0, 0)
            mToast!!.show()
        }
    }

    /**
     * 显示对话框
     *
     * @param showProgressBar 是否显示圈圈
     * @param msg             对话框信息
     */
    fun showProgressDialog(showProgressBar: Boolean, msg: String) {
        showProgressDialog(msg, true, null, showProgressBar)
    }

    /**
     * 显示进度对话框
     *
     * @param msg 消息
     */
    fun showProgressDialog(msg: String) {
        showProgressDialog(msg, true, null, true)
    }

    /**
     * 显示可取消的进度对话框
     *
     * @param msg 消息
     */
    fun showProgressDialog(
            msg: String, cancelable: Boolean,
            cancelListener: DialogInterface.OnCancelListener?,
            showProgressBar: Boolean
    ) {
        dismissProgressDialog()

        mActivity?.runOnUiThread(Runnable {
            if (mActivity == null || mActivity!!.isFinishing) {
                return@Runnable
            }
            mAlertDialog = GenericProgressDialog(mActivity, R.style.MyDialogStyleTop)
            mAlertDialog?.setMessage(msg)
            (mAlertDialog as GenericProgressDialog).setProgressVisiable(showProgressBar)
            mAlertDialog?.setCancelable(cancelable)
            mAlertDialog?.setOnCancelListener(cancelListener)
            mAlertDialog?.setCanceledOnTouchOutside(false)
            mAlertDialog?.show()
        })
    }

    fun dismissProgressDialog() {
        mActivity?.runOnUiThread {
            if (mAlertDialog != null && !mActivity!!.isFinishing) {
                mAlertDialog?.dismiss()
                mAlertDialog = null
            }
        }
    }
}