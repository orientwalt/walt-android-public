package com.yjy.wallet.bean.bch

data class OutputsItem(val addresses: List<String> = mutableListOf(),
                       val spentByTxPosition: Int = 0,
                       val type: String = "",
                       val value: Long = 0)