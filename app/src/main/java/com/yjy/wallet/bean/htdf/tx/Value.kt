package com.yjy.wallet.bean.htdf.tx

data class Value(val msg: List<MsgItem>?,
                 val fee: Fee,
                 val memo: String = "",
                 var delegator_address:String="",
                 var validator_address:String="",
                 val signatures: List<SignaturesItem>?)