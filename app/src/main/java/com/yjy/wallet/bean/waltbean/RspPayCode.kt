package com.yjy.wallet.bean.waltbean

import java.io.Serializable

data class RspPayCode(val amount: String = "",
                      val address: String = "",
                      val coin: String = "",
                      val remarks: String = "") : Serializable