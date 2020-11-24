package com.yjy.wallet.bean.bch

data class InputsItem(val sequence: Long = 0,
                      val prev_addresses: List<String>?,
                      val prev_tx_hash: String = "",
                      val prev_position: Int = 0,
                      val prev_type: String = "")