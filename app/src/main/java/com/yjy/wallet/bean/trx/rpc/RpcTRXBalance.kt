package com.yjy.wallet.bean.trx.rpc

data class RpcTRXBalance(val data: List<BDataItem> = mutableListOf(),
                         val success: Boolean = false,
                         val meta: Meta)