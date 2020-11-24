package com.yjy.wallet.bean.usdt

import java.io.Serializable

data class ReCharge(val token: String = "",
                    val txhash: String = "",
                    val type: String = "",
                    val number: String) : Serializable