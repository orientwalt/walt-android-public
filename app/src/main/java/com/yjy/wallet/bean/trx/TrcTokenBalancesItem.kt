package com.yjy.wallet.bean.trx

data class TrcTokenBalancesItem(val symbol: String = "",
                                val balance: String = "",
                                val decimals: Int = 0,
                                val name: String = "",
                                val contractAddress: String = "")