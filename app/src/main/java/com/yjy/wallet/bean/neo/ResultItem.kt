package com.yjy.wallet.bean.neo

data class ResultItem(val sendrawtransactionresult: Boolean = false,
                      val txid: String = "",
                      val errorMessage: String = "")