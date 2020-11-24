package com.yjy.wallet.bean.neo

data class BalanceItem(val unspent: List<UnspentItem> = mutableListOf(),
                       val amount: Double = 0.0,
                       val asset_hash: String = "",
                       val asset_symbol: String = "",
                       val asset: String = "")