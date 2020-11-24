package com.yjy.wallet.bean.trx.rpc

data class DataItem(val ret: List<RetItem> = mutableListOf(),
                    val block_timestamp: Long = 0,
                    val txID: String = "",
                    val raw_data: RawData)