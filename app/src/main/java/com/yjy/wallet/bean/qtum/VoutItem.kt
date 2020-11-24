package com.yjy.wallet.bean.qtum

data class VoutItem(val scriptPubKey: ScriptPubKey,
                    val spentIndex: Int = 0,
                    val spentHeight: Int = 0,
                    val spentTxId: String = "",
                    val value: String = "",
                    val n: Int = 0)