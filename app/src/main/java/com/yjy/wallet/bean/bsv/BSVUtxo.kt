package com.yjy.wallet.bean.bsv

data class BSVUtxo(val tx_pos: Long = 0,
                   val tx_hash: String = "",
                   val value: Long = 0,
                   val height: Int = 0)