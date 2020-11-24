package com.yjy.wallet.bean.htdf

import java.io.Serializable

data class Commission(var update_time: String = "",
                      var rate: Double,
                      var max_rate: Double,
                      var max_change_rate: Double):Serializable