package com.yjy.wallet.utils

import android.text.TextUtils
import com.weiyu.baselib.util.Utils

/**
 * weiweiyu
 * 2020/7/2
 * 575256725@qq.com
 * 13115284785
 */
class UnitUtils {
    companion object {
        fun unitConvert(num: String, rmb: String): String {
            var b = 0f
            if (!TextUtils.isEmpty(num)) {
                b = num.toFloat()
            }
            var a = 0f
            if (!TextUtils.isEmpty(rmb)) {
                a = rmb.toFloat()
            }
            var curentNum = a * b
            var moneyUnits = arrayOf("", "万", "亿", "万亿", "兆")
            var curentUnit = moneyUnits[0]
            for (i in 0..5) {
                curentUnit = moneyUnits[i]
                if (curentNum < 10000) {
                    break
                }
                curentNum = curentNum.div(10000f)
            }
            return Utils.toSubStringDegistForChartStr(curentNum.toString(), 2, false) + curentUnit
        }

        fun unitConvert(num: String): String {
            var curentNum = num.toFloat()
            var moneyUnits = arrayOf("", "万", "亿", "万亿", "兆")
            var curentUnit = moneyUnits[0]
            for (i in 0..5) {
                curentUnit = moneyUnits[i]
                if (curentNum < 10000) {
                    break
                }
                curentNum = curentNum.div(10000f)
            }
            return Utils.toSubStringDegistForChartStr(curentNum.toString(), 2, false) + curentUnit
        }

        fun times(num: String, rmb: String): String {
            return num.toFloat().times(rmb.toFloat()).toString()
        }
        fun getPrice(){

        }
    }

}