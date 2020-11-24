package com.yjy.wallet.bean.waltbean

data class AllLog(val number: String = "",
                  val amount: String = "",
                  val balance: String = "",
                  val id: Int = 0,
                  val type: Int = 0,
                  var coin: String = "",
                  var status:String="",
                  val remarks: String = "",
                  val savetime: String = "")