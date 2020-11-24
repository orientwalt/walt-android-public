package com.yjy.wallet.bean

import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 *Created by weiweiyu
 *on 2019/6/5
 * 地址本地址
 */
class Address(var type: Int, var address: String, var user: String, var remarck: String?, var time: Long) : LitePalSupport(), Serializable