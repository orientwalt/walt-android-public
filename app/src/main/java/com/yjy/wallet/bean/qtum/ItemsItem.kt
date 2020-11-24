package com.yjy.wallet.bean.qtum

data class ItemsItem(val fees: Double = 0.0,
                     val isqrcTransfer: Boolean = false,
                     val locktime: Int = 0,
                     val txid: String = "",
                     val confirmations: Int = 0,
                     val version: Int = 0,
                     val vout: MutableList<VoutItem> = mutableListOf(),
                     val blockheight: Int = 0,
                     val valueOut: Double = 0.0,
                     val blockhash: String = "",
                     val size: Int = 0,
                     val blocktime: Long = 0,
                     val valueIn: Double = 0.0,
                     val vin: MutableList<VinItem> = mutableListOf(),
                     val time: Long = 0)