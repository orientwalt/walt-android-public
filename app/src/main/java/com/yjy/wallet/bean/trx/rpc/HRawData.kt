package com.yjy.wallet.bean.trx.rpc

data class HRawData(val number: Long = 0,
                    val txTrieRoot: String = "",
                    val witnessAddress: String = "",
                    val parentHash: String = "",
                    val version: Int = 0,
                    val timestamp: Long = 0)