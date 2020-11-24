package com.yjy.wallet.bean.eth

data class DataItem(val balance: String = "",
                    val transferCnt: Int = 0,
                    val hash: String = "",
                    val tokenInfo: TokenInfo,
                    val network: String = "")