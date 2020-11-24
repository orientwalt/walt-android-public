package com.yjy.wallet.chainutils.htdf

import com.yjy.wallet.chainutils.usdp.AmountItem

data class MsgValue(val Amount: List<AmountItem>?,
                    val From: String = "",
                    val To: String = "",
                    var Data: String = "",
                    val GasPrice:String = "",
                    val GasWanted :String = "")