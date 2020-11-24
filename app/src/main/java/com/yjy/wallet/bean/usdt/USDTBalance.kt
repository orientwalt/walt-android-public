package com.yjy.wallet.bean.usdt

data class USDTBalance(val pendingpos: String = "",
                       val symbol: String = "",
                       val pendingneg: String = "",
                       val divisible: Boolean = false,
                       val id: String = "",
                       val error: Boolean = false,
                       val value: String = "")