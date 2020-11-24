package com.yjy.wallet.bean.params

import com.yjy.wallet.bean.htdf.Balance

class Base_req {
    var from: String? = null
    var memo: String? = null
    var password: String? = null
    var chain_id: String? = null
    var account_number: String? = null
    var sequence: String? = null
    var gas:String?=null
    var gas_adjustment:String ?=null
    var fees:List<Balance>?=null
    var simulate:Boolean?=null
}