package com.yjy.wallet.bean.trx.rpc

data class RpcTRXBlock(val blockID: String = "",
                       val block_header: BlockHeader)