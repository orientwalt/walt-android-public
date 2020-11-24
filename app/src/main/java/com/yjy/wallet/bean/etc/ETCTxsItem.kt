package com.yjy.wallet.bean.etc

data class ETCTxsItem(val gas_price: String = "",
                      val datetime: String = "",
                      val gas_used: String = "",
                      val from: String = "",
                      val block: Int = 0,
                      val to: String = "",
                      val confirmations: Int = 0,
                      val value: String = "",
                      val nonce: Int = 0,
                      val hash: String = "",
                      val timestamp: Int = 0)