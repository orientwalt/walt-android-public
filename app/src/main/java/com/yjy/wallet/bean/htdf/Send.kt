package com.yjy.wallet.bean.htdf

import java.io.Serializable

data class Send(val raw_log: String = "",
                val height: String = "",
                val txhash: String = "",
                val code: Long) : Serializable