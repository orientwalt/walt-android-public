package com.yjy.wallet.bean.btc

data class Utxo(val scriptPubKey: String = "",
                val amount: Double = 0.0,
                val address: String = "",
                val txid: String = "",
                val confirmations: Long = 0,
                val vout: Long = 0,
                val satoshis: Long = 0,
                val height: Int = 0)