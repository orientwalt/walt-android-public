package com.yjy.wallet.bean.trx

data class TRXBlock(val number: Long = 0,
                    val nrOfTrx: Int = 0,
                    val witnessAddress: String = "",
                    val size: Int = 0,
                    val txTrieRoot: String = "",
                    val parentHash: String = "",
                    val witnessId: Int = 0,
                    val confirmed: Boolean = false,
                    val hash: String = "",
                    val timestamp: Long = 0)