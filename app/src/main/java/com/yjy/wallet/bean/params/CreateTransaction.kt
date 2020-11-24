package com.yjy.wallet.bean.params

import com.yjy.wallet.bean.htdf.Balance

class CreateTransaction {
    var base_req: Base_req? = null
    var amount: List<Balance>? = null
    var to: String? = null
    var encode: Boolean? = null
}