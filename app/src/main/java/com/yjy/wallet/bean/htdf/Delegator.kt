package com.yjy.wallet.bean.htdf

data class Delegator(var error: String,
                     val shares: String = "",
                     val delegator_address: String = "",
                     val validatorAddress: String = "",
                     val status: Boolean = false)