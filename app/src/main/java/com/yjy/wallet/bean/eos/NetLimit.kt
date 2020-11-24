package com.yjy.wallet.bean.eos

data class NetLimit(val max: Int = 0,
                    val available: Int = 0,
                    val used: Int = 0)