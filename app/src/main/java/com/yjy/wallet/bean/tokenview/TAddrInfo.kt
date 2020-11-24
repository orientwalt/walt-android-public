package com.yjy.wallet.bean.tokenview

data class TAddrInfo(val balance: String = "",
                     val normalTxCount: Int = 0,
                     val addressType: String = "",
                     val rank: Int = 0,
                     val type: String = "",
                     val nonce: Int = 0,
                     val hash: String = "",
                     val network: String = "")