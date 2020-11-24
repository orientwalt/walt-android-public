package com.yjy.wallet.bean.bsv

data class VinItem(val sequence: Long = 0,
                   val scriptSig: ScriptSig,
                   val coinbase: String = "",
                   val txid: String = "",
                   val vout: Int = 0)