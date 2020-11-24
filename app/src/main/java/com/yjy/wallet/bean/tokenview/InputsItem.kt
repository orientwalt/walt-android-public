package com.yjy.wallet.bean.tokenview

data class InputsItem(val receivedFrom: ReceivedFrom,
                      val address: String = "",
                      val inputNo: Int = 0,
                      val value: String = "")