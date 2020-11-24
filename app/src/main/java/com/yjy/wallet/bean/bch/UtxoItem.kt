package com.yjy.wallet.bean.bch

data class UtxoItem(val tx_output_n: Long = 0,
                    val tx_output_n2: Int = 0,
                    val tx_hash: String = "",
                    val confirmations: Int = 0,
                    val value: Long = 0)