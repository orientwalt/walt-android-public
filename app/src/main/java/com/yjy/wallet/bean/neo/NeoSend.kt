package com.yjy.wallet.bean.neo

data class NeoSend(val result: List<ResultItem> = mutableListOf(),
                   val id: Int = 0,
                   val jsonrpc: String = "")