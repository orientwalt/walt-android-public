package com.yjy.wallet.bean.xrp

data class Meta(val affectedNodes: List<AffectedNodesItem>?,
                val transactionResult: String = "",
                val transactionIndex: Int = 0)