package com.weiyu.baselib.net

import android.content.Context
import com.weiyu.baselib.R

/**
 * weiweiyu
 * 2019/8/1
 * 575256725@qq.com
 * 13115284785
 * 错误信息
 */
class ErrorMsg {
    companion object {
        fun getErrMsgByCode(context: Context, code: Int?): String {
            return when (code) {
                10000 -> context.getString(R.string.err_10000)
                10001 -> context.getString(R.string.err_10001)
                10002 -> context.getString(R.string.err_10002)
                10003 -> context.getString(R.string.err_10003)
                10004 -> context.getString(R.string.err_10004)
                10005 -> context.getString(R.string.err_10005)
                10006 -> context.getString(R.string.err_10006)
                10007 -> context.getString(R.string.err_10007)
                10008 -> context.getString(R.string.err_10008)
                10009 -> context.getString(R.string.err_10009)
                10010 -> context.getString(R.string.err_10010)
                10011 -> context.getString(R.string.err_10011)
                10012 -> context.getString(R.string.err_10012)
                10013 -> context.getString(R.string.err_10013)
                10014 -> context.getString(R.string.err_10014)
                10015 -> context.getString(R.string.err_10015)
                10016 -> context.getString(R.string.err_10016)
                10017 -> context.getString(R.string.err_10017)
                10018 -> context.getString(R.string.err_10018)
                10019 -> context.getString(R.string.err_10019)
                10020 -> context.getString(R.string.err_10020)
                10021 -> context.getString(R.string.err_10021)
                10022 -> context.getString(R.string.err_10022)
                10023 -> context.getString(R.string.err_10023)
                10024 -> context.getString(R.string.err_10024)
                10025 -> context.getString(R.string.err_10025)
                10026 -> context.getString(R.string.err_10026)
                10027 -> context.getString(R.string.err_10027)
                10028 -> context.getString(R.string.err_10028)
                10029 -> context.getString(R.string.err_10029)
                10030 -> context.getString(R.string.err_10030)
                10031 -> context.getString(R.string.err_10031)
                10032 -> context.getString(R.string.err_10032)
                10033 -> context.getString(R.string.err_10033)
                10034 -> context.getString(R.string.err_10034)
                10035 -> context.getString(R.string.err_10035)
                10036 -> context.getString(R.string.err_10036)
                10037 -> context.getString(R.string.err_10037)
                10038 -> context.getString(R.string.err_10038)
                10039 -> context.getString(R.string.err_10039)
                10040 -> context.getString(R.string.err_10040)
                10041 -> context.getString(R.string.err_10041)
                10042 -> context.getString(R.string.err_10042)
                10043 -> context.getString(R.string.err_10043)
                10044 -> context.getString(R.string.err_10044)
                10045 -> context.getString(R.string.err_10045)
                10046 -> context.getString(R.string.err_10046)
                10047 -> context.getString(R.string.err_10047)
                10048 -> context.getString(R.string.err_10048)
                10049 -> context.getString(R.string.err_10049)
                10050 -> context.getString(R.string.err_10050)
                10051 -> context.getString(R.string.err_10051)
                else -> ""
            }
        }
    }
}
