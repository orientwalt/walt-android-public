package com.yjy.wallet.bean.trx.rpc

data class BlockHeader(val raw_data: HRawData,
                       val witness_signature: String = "")