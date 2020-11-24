package com.yjy.wallet.bean.tokenview

data class TTokenTx(val tokenSymbol: String = "",
                    val index: Int = 0,
                    val txid: String = "",
                    val conformations: Int = 0,
                    val tokenAddr: String = "",
                    val tokenInfo: TokenInfo,
                    val token: String = "",
                    val block_no: Int = 0,
                    val tokenDecimals: String = "",
                    val from: String = "",
                    val time: Long = 0,
                    val to: String = "",
                    val value: String = "")