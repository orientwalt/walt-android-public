package com.yjy.wallet.bean.tokenview

data class TUTx(val outputs: List<OutputsItem>?,
                val outputCnt: Int = 0,
                val inputs: List<InputsItem>?,
                val fee: String = "",
                val index: Int = 0,
                val txid: String = "",
                val type: String = "",
                val confirmations: Int = 0,
                val network: String = "",
                val blockNo: Int = 0,
                val inputCnt: Int = 0,
                val time: Long = 0,
                val height: Int = 0)