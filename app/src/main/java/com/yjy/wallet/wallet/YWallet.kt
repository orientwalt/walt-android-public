package com.yjy.wallet.wallet

import java.io.Serializable

/**
 *Created by weiweiyu
 *on 2019/4/30
 */
data class YWallet(var pwd: String? = null, //钱包密码MD5
                   var remark: String? = null, //钱包备注
                   var wType: Int = 0//100助记词钱包 else 类型钱包 类型参考WaltType
) : Serializable {
    var words: String? = ""
    var map: MutableMap<String, WInfo> = mutableMapOf()//这样设计因为第一版是生成多链钱包，后面可能还得需要改成多链
    var ercmMap: MutableMap<String, WInfo?> = mutableMapOf()
    var wenMap: MutableMap<String, WInfo?> = mutableMapOf()
    var htdfercmMap: MutableMap<String, WInfo?> = mutableMapOf()
    var show = false
    var htdf = false
}