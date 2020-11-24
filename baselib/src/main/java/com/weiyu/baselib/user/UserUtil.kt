package com.weiyu.baselib.user

import android.text.TextUtils
import com.google.gson.Gson
import com.weiyu.baselib.util.PrefUtils

class UserUtil private constructor() {
    private var userdata by PrefUtils("userinfo", "")

    companion object StaticParams {
        val instance: UserUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            UserUtil()
        }
    }

    fun saveUserInfo(u: User?) {
        if (u == null) {
            return
        }
        var data = userdata
        var userinfo: UserInfo
        userinfo = if (TextUtils.isEmpty(data)) {
            UserInfo()
        } else {
            Gson().fromJson(data, UserInfo::class.java)
        }
        u.usercode=u.phone?:u.email?:""
        userinfo.user = u
        userinfo.lastUser = u
        userinfo.uList[u.usercode] = u
        var jsonStr = Gson().toJson(userinfo)
        userdata = jsonStr
    }

    fun getUser(): User? {
        var data = userdata
        if (TextUtils.isEmpty(data)) {
            return null
        }
        var userInfo: UserInfo = Gson().fromJson(data, UserInfo::class.java)
        return userInfo.user
    }

    fun getLastUser(): User? {
        var data = userdata
        if (TextUtils.isEmpty(data)) {
            return null
        }
        var userInfo: UserInfo = Gson().fromJson(data, UserInfo::class.java)
        return userInfo.lastUser
    }

    fun remove() {
        var data = userdata
        if (!TextUtils.isEmpty(data)) {
            var userInfo: UserInfo = Gson().fromJson(data, UserInfo::class.java)
            userInfo.user = null
            var jsonStr = Gson().toJson(userInfo)
            userdata = jsonStr
        }
    }

    fun getUser(key: String): User? {
        var data = userdata
        if (TextUtils.isEmpty(data)) {
            return null
        }
        var userInfo: UserInfo = Gson().fromJson(data, UserInfo::class.java)
        return userInfo.uList[key]
    }
}