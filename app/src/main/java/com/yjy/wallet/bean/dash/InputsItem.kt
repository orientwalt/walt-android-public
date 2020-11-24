package com.yjy.wallet.bean.dash

data class InputsItem(val sequence: Long = 0,
                      val scriptSig: ScriptSig,
                      val valueSat: Int = 0,
                      val txid: String = "",
                      val addr: String = "",
                      val value: Double = 0.0,
                      val n: Int = 0,
                      val vout: Int = 0)