package com.yjy.wallet.wallet

import java.io.Serializable

/**
 *Created by weiweiyu
 *on 2019/4/30
 */
data class WInfo(var keyPath: String,//keystone路径
                 var keystone: String, //keystone字符串最开始是使用keystone格式，3.0版本后改为aes加密
                 var address: String,//地址
                 var account_number: String,//账号id
                 var sequence: String,//交易次数
                 var balance: String,//余额
                 var unit: String//币种
) : Serializable {
    var old = true//旧版保存keystonejson , 新版保存asc加密字符串加快了保存速度
    var hight = 0L
    var rmb: Double = 0.0
    var show = true
    var contract_address = ""//合约地址
    var decimal = 0
    var custom = false
    var eospkey = ""
}
