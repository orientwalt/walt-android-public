package com.yjy.wallet.bean.neo

data class UnspentItem(val txid: String = "",
                       val value: Double = 0.0,
                       val n: Int = 0)