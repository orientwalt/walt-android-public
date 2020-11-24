package com.yjy.wallet.bean.xrp

data class Result1(val marker: Marker,
                   val ledger_index_max: Long = 0,
                   val limit: Int = 0,
                   val ledger_index_min: Long = 0,
                   val transactions: List<TransactionsItem>?,
                   val account: String = "",
                   val status: String = "")