package com.yjy.wallet.bean.neo

data class EntriesItem(val amount: String = "",
                       val address_to: String = "",
                       val txid: String = "",
                       val time: Long = 0,
                       val block_height: Int = 0,
                       val asset: String = "",
                       val address_from: String = "")