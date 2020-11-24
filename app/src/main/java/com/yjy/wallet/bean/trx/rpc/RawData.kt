package com.yjy.wallet.bean.trx.rpc

data class RawData(val contract: List<ContractItem> = mutableListOf(),
                   val ref_block_bytes: String = "",
                   val ref_block_hash: String = "",
                   val expiration: Long = 0,
                   val fee_limit: Long = 0,
                   val timestamp: Long = 0)