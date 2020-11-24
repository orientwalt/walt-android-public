package com.yjy.wallet.bean.qtum

data class QtumUtxo(val scriptPubKey: String = "",
                    val amount: String = "",
                    val address: String = "",
                    val isStake: Boolean = false,
                    val txid: String = "",
                    val confirmations: Int = 0,
                    val vout: Int = 0,
                    val satoshis: Long = 0,
                    val height: Int = 0)