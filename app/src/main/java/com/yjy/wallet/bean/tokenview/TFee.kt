package com.yjy.wallet.bean.tokenview

data class TFee(val bestGasPrice: String = "",
                val totalCnt: Int = 0,
                val minGasPrice: String = "",
                var mediumTxByte: Long = 0,
                var bestTxFee: String = "",
                val totalSentValue: String = "",
                val totalFee: String = "",
                val maxGasPrice: Long = 0,
                val totalCallTransferCnt: Int = 0,
                val totalTokenTransferCnt: Int = 0)