package com.yjy.wallet.bean.bsv

data class ScriptPubKey(val addresses: List<String>?,
                        val asm: String = "",
                        val hex: String = "",
                        val type: Int = 0,
                        val opReturn: Any? = null,
                        val reqSigs: Int = 0)