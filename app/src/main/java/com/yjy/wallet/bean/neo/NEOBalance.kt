package com.yjy.wallet.bean.neo

data class NEOBalance(val address: String = "",
                      val balance: MutableList<BalanceItem> = mutableListOf())