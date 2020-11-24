package com.yjy.wallet.bean.xrp

data class Result(val ledger_current_index: Int = 0,
                  val validated: Boolean = false,
                  val account_data: AccountData,
                  val queue_data: QueueData,
                  val status: String = "")