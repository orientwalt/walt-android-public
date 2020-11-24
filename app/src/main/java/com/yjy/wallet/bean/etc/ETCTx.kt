package com.yjy.wallet.bean.etc

data class ETCTx(val amount: String = "",
                 val unit: String = "",
                 val datetime: String = "",
                 val fee: String = "",
                 val received: String = "",
                 val confirmations: Int = 0,
                 val sent: String = "",
                 val timestamp: Int = 0)