package com.weiyu.baselib.net

class BaseResult<T> {
    var code: Int? = null
    var msg: String? = null
    var err_code: Int = 0
    var err_msg: String = ""
    var data: T? = null
}