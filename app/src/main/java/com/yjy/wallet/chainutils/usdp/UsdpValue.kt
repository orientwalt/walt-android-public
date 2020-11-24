package com.yjy.wallet.chainutils.usdp

data class UsdpValue(var msg: List<Msg>, var fee: Fees, var signatures: List<Signatures>?=null, var memo: String)