package com.yjy.wallet.bean

import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 *Created by weiweiyu
 *on 2019/5/14
 */
class TxItem(var Height: String? = null,//高度
             var Hash: String? = null,//交易哈希号
             var From: String? = null,//转出地址
             var To: String? = null,//转入地址
             var amount: String? = null,//转出数量
             var denom: String? = null,//币种
             var msg: String? = null,//备注
             var Time: Long = 0L,//时间
             var isMain: Boolean = true) : LitePalSupport(), Serializable {
    var comfirm = 0
    var state = 0
    var fee = 0L
    var type = "0"
    var token = ""
}