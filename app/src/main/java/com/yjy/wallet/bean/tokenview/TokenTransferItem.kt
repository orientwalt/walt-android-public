package com.yjy.wallet.bean.tokenview

data class TokenTransferItem(val tokenSymbol: String = "",
                             val tokenDecimals: String = "",
                             val index: Int = 0,
                             val from: String = "",
                             val to: String = "",
                             val tokenAddr: String = "",
                             val value: String = "",
                             val tokenInfo: TokenInfo,
                             val token: String = "")