package com.yjy.wallet.bean.dash

data class DashBalance(val txid: String = "",
                       val vout: Long = 0,
                       val address: String = "",
                       val scriptPubKey: String = "",
                       val amount: Double = 0.0,
                       var satoshis: Long = 0,
                       val height: Int = 0,
                       val confirmations: Long = 0
)