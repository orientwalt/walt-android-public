package com.yjy.wallet.bean.bsv

data class BSVTx(val blockhash: String = "",
                 val size: Int = 0,
                 val locktime: Int = 0,
                 val blocktime: Int = 0,
                 val txid: String = "",
                 val vin: List<VinItem>?,
                 val time: Long = 0,
                 val confirmations: Int = 0,
                 val version: Int = 0,
                 val hash: String = "",
                 val vout: List<VoutItem>?)