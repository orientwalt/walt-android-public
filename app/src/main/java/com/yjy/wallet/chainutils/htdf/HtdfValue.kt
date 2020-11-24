package com.yjy.wallet.chainutils.htdf

import com.yjy.wallet.chainutils.usdp.Signatures

data class HtdfValue(var msg: List<Msg>, var fee: Fees, var signatures: List<Signatures>?=null, var memo: String)