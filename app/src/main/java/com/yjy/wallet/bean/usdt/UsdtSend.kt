package com.yjy.wallet.bean.usdt

data class UsdtSend(val tx: String = "",
                    val pushed: String = "",
                    var message:String="",
                    val status: String = "")