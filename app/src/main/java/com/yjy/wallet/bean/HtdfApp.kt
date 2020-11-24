package com.yjy.wallet.bean

import java.io.Serializable

data class HtdfApp(val delegatorAddress: String = "",
                   val amount: String = "",
                   val updateTime: String = "",
                   val validatorAddress: String = "",
                   val channel: Int = 0,
                   val remark: String = "",
                   val id: Int = 0,
                   val time: String = "",
                   val serverId: Int = 0,
                   val type: Int = 0,
                   val txhash: String = ""):Serializable