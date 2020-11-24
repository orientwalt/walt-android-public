package com.yjy.wallet.bean.trx.rpc

data class BDataItem(val address: String = "",
                     val balance: Long = 0,
                     val create_time: Long = 0)