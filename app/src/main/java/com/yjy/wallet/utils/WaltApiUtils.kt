package com.yjy.wallet.utils

import com.weiyu.baselib.util.Des3
import com.weiyu.baselib.util.MD5Util
import org.json.JSONObject

/**
 *Created by weiweiyu
 * 575256725@qq.com
 *on 2019/7/5
 */
class WaltApiUtils {
    var key = "2E63C38FA967B2F14BAA3DAF5A443F46"
    fun getApiMap(map: MutableMap<String, Any>): MutableMap<String, Any> {
        var sb = StringBuffer()
        map.forEach {
            sb.append(it.key).append("=").append(it.value).append("&")
        }
        sb.append("key=").append(key)
        map["sign"] = MD5Util.getMD5UpperString(sb.toString())
        return map
    }

    fun getApiJson(map: MutableMap<String, Any>): String {
        var sb = StringBuffer()
        var json = JSONObject()
        map.forEach {
            sb.append(it.key).append("=").append(it.value).append("&")
            json.put(it.key, it.value)
        }
        sb.append("key=").append(key)
        json.put("sign", MD5Util.getMD5UpperString(sb.toString()))
        return Des3.encryptAES(json.toString()).replace(" ", "").replace("\n","")
    }
}