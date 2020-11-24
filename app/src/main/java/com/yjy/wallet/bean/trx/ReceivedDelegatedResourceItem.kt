package com.yjy.wallet.bean.trx

data class ReceivedDelegatedResourceItem(val expire_time_for_energy: Long = 0,
                                         val frozen_balance_for_energy: Long = 0,
                                         val from: String = "",
                                         val to: String = "")