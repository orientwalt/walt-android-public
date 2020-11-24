package com.yjy.wallet.bean.qtum

data class VinItem(val sequence: Long = 0,
                   val scriptSig: ScriptSig,
                   val valueSat: Long = 0,
                   val txid: String = "",
                   val addr: String = "",
                   val doubleSpentTxID: String = "",
                   val value: Double = 0.0,
                   val n: Int = 0,
                   val vout: Int = 0)