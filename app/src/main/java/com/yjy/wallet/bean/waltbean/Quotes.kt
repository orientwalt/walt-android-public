package com.yjy.wallet.bean.waltbean

data class Quotes(val number: Int = 0,
                  val limit: String = "",
                  val page: Int = 0,
                  var info: MutableList<InfoItem> = mutableListOf())