package com.weiyu.baselib.base

import android.content.DialogInterface

interface ActivityResponsable {
    /**
     * 弹对话框
     *
     * @param title
     * 标题
     * @param msg
     * 消息
     * @param positive
     * 确定
     * @param positiveListener
     * 确定回调
     * @param negative
     * 否定
     * @param negativeListener
     * 否定回调
     */
    fun alert(
            title: String, msg: String, positive: String,
            positiveListener: DialogInterface.OnClickListener,
            negative: String, negativeListener: DialogInterface.OnClickListener
    )

    /**
     * 弹对话框
     *
     * @param title
     * 标题
     * @param msg
     * 消息
     * @param positive
     * 确定
     * @param positiveListener
     * 确定回调
     * @param negative
     * 否定
     * @param negativeListener
     * 否定回调
     * @param isCanceledOnTouchOutside
     * 是否外部点击取消
     */
    fun alert(
            title: String, msg: String, positive: String,
            positiveListener: DialogInterface.OnClickListener,
            negative: String,
            negativeListener: DialogInterface.OnClickListener,
            isCanceledOnTouchOutside: Boolean?
    )

    /**
     * TOAST
     *
     * @param msg
     * 消息
     * @param period
     * 时长
     */
    fun toast(msg: String, period: Int)

    fun toast(msg: String)

    fun toastPosition(msg: String, period: Int)
    /**
     * 显示进度对话框
     *
     * @param msg
     * 消息
     */
    fun showProgressDialog(msg: String)

    /**
     * 显示可取消的进度对话框
     *
     * @param msg
     * 消息
     */
    fun showProgressDialog(
            msg: String, cancelable: Boolean,
            cancelListener: DialogInterface.OnCancelListener
    )

    /**
     * 取消进度对话框
     *
     */
    fun dismissProgressDialog()
}