package com.yjy.wallet.bean.cxc

data class Result(val blockhash: String = "",
                  val locktime: Long = 0,
                  val blocktime: Long = 0,
                  val txid: String = "",
                  val hex: String = "",
                  val time: Long = 0,
                  val confirmations: Long = 0,
                  val version: Int = 0)