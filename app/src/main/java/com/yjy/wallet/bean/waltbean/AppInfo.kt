package com.yjy.wallet.bean.waltbean

data class AppInfo(val delegatorAddress: String = "",
                   val tq_txhash: String = "",
                   val amount: String = "",
                   val sh_time: String = "",
                   val channel: Int = 0,
                   val remark: String = "",
                   var server_name:String = "",
                   val server_id: Int = 0,
                   val type: Int = 0,
                   val tq_dztime: String = "",
                   val txhash: String = "",
                   val updateTime: String = "",
                   val validator_address: String = "",
                   val tq_time: String = "",
                   val id: Int = 0,
                   val time: String = "")