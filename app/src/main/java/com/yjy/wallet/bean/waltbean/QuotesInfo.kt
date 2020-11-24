package com.yjy.wallet.bean.waltbean

data class QuotesInfo(val high: String = "0.0",
                      val siteurl: String = "",
                      val low: String = "0.0",
                      val totalSupply: String = "0",
                      val rank: Int = 0,
                      var marketcap: String = "0",
                      var vol: String = "0",
                      val supply: String = "0")