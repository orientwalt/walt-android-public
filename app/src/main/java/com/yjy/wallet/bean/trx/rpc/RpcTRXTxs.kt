package com.yjy.wallet.bean.trx.rpc

data class RpcTRXTxs(val data: List<DataItem> = mutableListOf(),
                     val success: Boolean = false,
                     val meta: Meta)