package com.yjy.wallet.bean.dash

data class DashTx(var txid: String = "",
                  var version: Int = 0,
                  var locktime: Long,
                  var vin: List<InputsItem> = mutableListOf(),
                  var vout: List<OutputsItem> = mutableListOf(),
                  var blockhash: String = "",
                  var blockheight: Int = 0,
                  var confirmations: Int = 0,
                  var time: Long = 0,
                  var blocktime: Long = 0,
                  var valueOut: Double = 0.0,
                  var valueIn: Double = 0.0,
                  var size: Int = 0,
                  var fees: Double = 0.0,
                  var txlock: Boolean = true)