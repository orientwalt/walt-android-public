package com.yjy.wallet.bean.htdf.tx

data class Htdftx(val tx: Tx,
                  val gas_used: String = "",
                  val gas_wanted: String = "",
                  val raw_log: String = "",
                  val logs: List<LogsItem>?,
                  val height: String = "",
                  val txhash: String = "",
                  val tags: List<TagsItem>?,
                  val timestamp: String = "")