package com.yjy.wallet.bean.eos

data class Error(val code: Int = 0,
                 val what: String = "",
                 val name: String = "",
                 val details: List<DetailsItem>?)