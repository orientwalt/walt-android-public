package com.yjy.wallet.bean.trx.rpc

data class Value(val amount: Long = 0,
                 val owner_address: String = "",
                 val to_address: String = "")