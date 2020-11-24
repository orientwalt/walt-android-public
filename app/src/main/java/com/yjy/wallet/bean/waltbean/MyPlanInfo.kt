package com.yjy.wallet.bean.waltbean

data class MyPlanInfo(val payAddress: String = "",
                      val expireTime: String = "",
                      val balance: String = "",
                      val buyTime: String = "",
                      val planid: Int = 0,
                      val collAddress: String = "",
                      val profit:String="",
                      val status: Int = 0)