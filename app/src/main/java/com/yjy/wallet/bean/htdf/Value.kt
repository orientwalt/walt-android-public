package com.yjy.wallet.bean.htdf

data class Value(val publicKey: String? = null,
                 val account_number: String = "",
                 val sequence: String = "",
                 val address: String = "",
                 val coins: MutableList<Balance>?)