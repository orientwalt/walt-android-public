package com.yjy.wallet.bean.eth

data class TokenTx(val result: MutableList<TokenItem> = arrayListOf(),
                   val message: String = "",
                   val status: String = "")