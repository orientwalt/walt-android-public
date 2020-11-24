package com.weiyu.baselib.base

import android.app.Activity
import android.content.DialogInterface

class ActivityHelper constructor(mActivity: Activity) {

    /**
     * 对应的Activity
     */
    private var mActivity: Activity = mActivity

    /**
     * 对话框帮助类
     */
    private var mDialogHelper: DialogHelper

    init {
        this.mActivity = mActivity
        mDialogHelper = DialogHelper(mActivity)
    }

    fun finish() {
        mDialogHelper.dismissProgressDialog()
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
            positiveListener: DialogInterface.OnClickListener, negative: String,
            negativeListener: DialogInterface.OnClickListener
    ) {
        mDialogHelper.alert(title, msg, positive, positiveListener, negative, negativeListener)
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
     * @param isCanceledOnTouchOutside 外部是否可点取消
     */
    fun alert(
            title: String, msg: String, positive: String,
            positiveListener: DialogInterface.OnClickListener, negative: String,
            negativeListener: DialogInterface.OnClickListener,
            isCanceledOnTouchOutside: Boolean?
    ) {
        mDialogHelper.alert(
                title, msg, positive, positiveListener, negative, negativeListener,
                isCanceledOnTouchOutside
        )
    }

    /**
     * TOAST
     *
     * @param msg    消息
     * @param period 时长
     */
    fun toast(msg: String, period: Int) {
        mDialogHelper.toast(msg, period)
    }

    /**
     * TOAST
     *
     * @param msg    消息
     */
    fun toast(msg: String) {
        mDialogHelper.toast(msg, 0)
    }

    fun toastPosition(msg: String, position: Int) {
        mDialogHelper.toast(msg, 0, position)
    }

    /**
     * 显示进度对话框
     *
     * @param msg 消息
     */
    fun showProgressDialog(msg: String) {
        mDialogHelper.showProgressDialog(msg)
    }

    /**
     * 显示可取消的进度对话框
     *
     * @param msg 消息
     */
    fun showProgressDialog(
            msg: String, cancelable: Boolean,
            cancelListener: DialogInterface.OnCancelListener
    ) {
        mDialogHelper.showProgressDialog(msg, cancelable, cancelListener, true)
    }

    fun dismissProgressDialog() {
        mDialogHelper.dismissProgressDialog()
    }
}