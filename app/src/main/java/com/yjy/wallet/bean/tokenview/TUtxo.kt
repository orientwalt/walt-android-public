package com.yjy.wallet.bean.tokenview

data class TUtxo(val block_no: Int = 0,
                 val output_no: Long = 0,
                 val index: String = "",
                 val txid: String = "",
                 var hex:String="",
                 val confirmations: Int = 0,
                 val value: String = "")