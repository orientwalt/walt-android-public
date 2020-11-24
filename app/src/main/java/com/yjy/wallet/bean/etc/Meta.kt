package com.yjy.wallet.bean.etc

data class Meta(val limit: Int = 0,
                val index: Int = 0,
                val total_count: Int = 0,
                val error: Error? = null,
                val results: Int = 0)

class Error(
        var message: String,
        var code: Int
)