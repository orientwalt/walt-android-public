package com.yjy.wallet.bean.waltbean

import java.io.Serializable

data class InfoItem2(
        val symbol: String = "",
        val name: String = "",
        var price_cny: String = "0") : Serializable