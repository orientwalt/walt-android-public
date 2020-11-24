package com.yjy.wallet.bean.qtum

data class ScriptPubKey(val addresses: List<String>?,
                        val hex: String = "",
                        val asm: String = "",
                        val type: String = "")