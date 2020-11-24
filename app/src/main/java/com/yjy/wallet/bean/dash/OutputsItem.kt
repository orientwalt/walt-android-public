package com.yjy.wallet.bean.dash

data class OutputsItem(val scriptPubKey: ScriptPubKey,
                       val value: String = "",
                       val n: Int = 0)