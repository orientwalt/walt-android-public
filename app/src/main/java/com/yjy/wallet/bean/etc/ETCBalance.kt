package com.yjy.wallet.bean.etc

data class ETCBalance(val txs_count: Int = 0,
                      val chain: String = "",
                      val address: String = "",
                      val balance: String = "",
                      val from: Int = 0,
                      val to: Int = 0)