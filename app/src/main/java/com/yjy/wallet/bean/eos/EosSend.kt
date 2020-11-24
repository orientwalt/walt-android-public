package com.yjy.wallet.bean.eos

data class EosSend(val code: Int = 0,
                   val message: String = "",
                   val error: Error)