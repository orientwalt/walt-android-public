package com.yjy.wallet.bean.cxc

data class CUtxo(val hex: String = "",
                 val value: String = "",
                 val address: String = "",
                 val n: Int = 0,
                 val txhash: String = "")