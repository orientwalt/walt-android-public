package com.yjy.wallet.bean.cxc

data class CTx(val blockhash: String = "",
               val address: String = "",
               val blocktime: Int = 0,
               val txhash: String = "",
               val time: Long = 0,
               val value: String = "",
               val confirmations: Long = 0,
               val blockheight: Long = 0,
               val vin: List<CUtxo>? = null,
               val vout: List<CUtxo>? = null,
               val status: Int = 0)