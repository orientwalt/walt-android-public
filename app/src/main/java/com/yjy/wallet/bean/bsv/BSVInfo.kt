package com.yjy.wallet.bean.bsv

data class BSVInfo(val scriptPubKey: String = "",
                   val address: String = "",
                   val isscript: Boolean = false,
                   val ismine: Boolean = false,
                   val isvalid: Boolean = false,
                   val iswatchonly: Boolean = false)