package com.weiyu.baselib.util

import android.util.Log
import com.weiyu.baselib.BuildConfig

class BLog {
    companion object StaticParams {
        var isDebug = BuildConfig.DEBUG
        private val TAG = "WLog"

        // 下面四个是默认tag的函数
        fun i(msg: String) {
            if (isDebug)
                Log.i(TAG, msg)
        }

        fun d(msg: String) {
            if (isDebug)
                Log.d(TAG, msg)
        }

        fun e(msg: String) {
            if (isDebug)
                Log.e(TAG, msg)
        }

        fun v(msg: String) {
            if (isDebug)
                Log.v(TAG, msg)
        }

        // 下面是传入类名打印log
        fun i(_class: Class<*>, msg: String) {
            if (isDebug)
                Log.i(_class.name, msg)
        }

        fun d(_class: Class<*>, msg: String) {
            if (isDebug)
                Log.i(_class.name, msg)
        }

        fun e(_class: Class<*>, msg: String) {
            if (isDebug)
                Log.i(_class.name, msg)
        }

        fun v(_class: Class<*>, msg: String) {
            if (isDebug)
                Log.i(_class.name, msg)
        }

        // 下面是传入自定义tag的函数
        fun i(tag: String, msg: String) {
            if (isDebug)
                Log.i(tag, msg)
        }

        fun d(tag: String, msg: String) {
            if (isDebug)
                Log.i(tag, msg)
        }

        fun e(tag: String, msg: String) {
            if (isDebug)
                Log.i(tag, msg)
        }

        fun v(tag: String, msg: String) {
            if (isDebug)
                Log.i(tag, msg)
        }
    }
}