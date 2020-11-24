package com.yjy.wallet.bean.trx.rpc

data class RpcTRXTXInfo(val blockNumber: Int = 0,
                        val contractResult: List<String>?,
                        val blockTimeStamp: Long = 0,
                        val receipt: Receipt,
                        val id: String = "")