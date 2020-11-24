package com.yjy.wallet.bean.eos

data class Receipt(val cpu_usage_us: Int = 0,
                   val net_usage_words: Int = 0,
                   val status: String = "")