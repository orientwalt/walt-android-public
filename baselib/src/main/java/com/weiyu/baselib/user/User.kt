package com.weiyu.baselib.user

class User {
    var phone: String? = ""
    var email: String? = ""
    var usercode: String = ""//账号
    var token: String = ""
    var role: String? = null//角色（0：普通用户；1：商户；）
    var status: String? = null//账号类型(1:邮箱；2：手机号码)
    var type: String? = null//支付设置
    var cardcode: String? = null//证件类型
    var truename: String? = null//真实姓名
    var idencode: String? = null//证件号码
    var cardurl2: String? = null//身份证反面
    var cardurl1: String? = null//身份证正面
    var cardurl3: String? = null//身份证正面
    var content: String? = null//个性签名
    var sex: String? = null//性别（1：男来：女）
    var nickname: String? = null//昵称
    var head: String? = null//头像
    var id: String? = null
    var open: String? = "0"
    var tradepwd: String = "0"
    var loadpwd: String = "0"
    var cardurlcode: String = "0"
    var walt: String = ""
    var again: String = "0"
    var recomid: String = "0"
    var refer: String = ""
    var isLogin = true
    var remarks = ""
}