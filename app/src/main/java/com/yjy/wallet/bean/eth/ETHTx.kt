package com.yjy.wallet.bean.eth

data class ETHTx(val result: MutableList<ResultItem> = arrayListOf(),
                 val message: String = "",
                 val status: String = "")