package com.yjy.wallet.bean.waltbean

data class PayLog(val amount: String = "",
                  val id: Int = 0,
                  val remarks: String = "",
                  val status: String = "",
                  val savetime: String = "",
                  var number: String = "",
                  var listid: String = "",
                  var type: Int,
                  var coin: String = "",
                  var tohead: String,
                  var tonickname: String,
                  var fromhead: String,
                  var fromnickname: String)