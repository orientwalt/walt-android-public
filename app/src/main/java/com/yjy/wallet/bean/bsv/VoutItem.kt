package com.yjy.wallet.bean.bsv

data class VoutItem(val scriptPubKey: ScriptPubKey,
                    val value: Double = 0.0,
                    val n: Int = 0)