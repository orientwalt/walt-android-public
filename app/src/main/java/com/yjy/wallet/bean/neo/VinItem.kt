package com.yjy.wallet.bean.neo

data class VinItem(val addressHash: String = "",
                   val txid: String = "",
                   val asset: String = "",
                   val value: Int = 0,
                   val n: Int = 0)