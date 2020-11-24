package com.yjy.wallet.bean.waltbean

import java.io.Serializable

/**
 *Created by weiweiyu
 * 575256725@qq.com
 *on 2019/7/12
 */
data class RspWalt(var id: String, var name: String, var address: String, var status: String, var coin: String, var amount: String = "0.0", var type: String, var number: String) : Serializable