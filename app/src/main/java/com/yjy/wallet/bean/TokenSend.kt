package com.yjy.wallet.bean

data class TokenSend(val result: String = "",
                     val id: String = "",
                     var error: Error? = null,
                     val jsonrpc: String = "")