package com.yjy.wallet.bean.waltbean

data class HBAllItem(val symbol: String = "",
                     val high: Double = 0.0,
                     val amount: Double = 0.0,
                     val vol: Double = 0.0,
                     val low: Double = 0.0,
                     val bidSize: Double = 0.0,
                     val count: Int = 0,
                     val ask: Double = 0.0,
                     val bid: Double = 0.0,
                     val close: Double = 0.0,
                     val open: Double = 0.0,
                     val askSize: Double = 0.0)