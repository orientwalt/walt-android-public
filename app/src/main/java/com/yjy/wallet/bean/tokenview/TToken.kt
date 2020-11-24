package com.yjy.wallet.bean.tokenview

data class TToken(val holderCnt: Int = 0,
                  val transferCnt: Int = 0,
                  val tokenHash: String = "",
                  val tokenInfo: TokenInfo,
                  val network: String = "")