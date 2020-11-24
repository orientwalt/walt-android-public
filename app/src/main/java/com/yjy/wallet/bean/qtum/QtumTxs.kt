package com.yjy.wallet.bean.qtum

data class QtumTxs(val totalItems: Int = 0,
                   val from: Int = 0,
                   val to: Int = 0,
                   val items: MutableList<ItemsItem> = mutableListOf())