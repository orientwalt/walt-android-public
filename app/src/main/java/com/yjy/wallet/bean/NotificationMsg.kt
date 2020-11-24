package com.yjy.wallet.bean

import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 * weiweiyu
 * 2020/7/8
 * 575256725@qq.com
 * 13115284785
 */
class NotificationMsg : LitePalSupport(), Serializable {
    var time: Long = 0
    var read: Int = 0//0未读 1已读
    var txid: String = ""
    var noticeType = 0
    var unit: String = ""
    var value: String = ""
    var address: String = ""
    var token: String = ""
}