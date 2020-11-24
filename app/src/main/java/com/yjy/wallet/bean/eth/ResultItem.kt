package com.yjy.wallet.bean.eth

data class ResultItem(val timeStamp: String = "",
                      val input: String = "",
                      val gasUsed: String = "",
                      val isError: String = "",
                      var hash: String = "",
                      val errCode: String = "",
                      val blockNumber: String = "",
                      val gas: String = "",
                      var gasPrice: String = "",
                      var confirmations:String="0",
                      val contractAddress: String = "",
                      val from: String = "",
                      val to: String = "",
                      val type: String = "",
                      val value: String = "")