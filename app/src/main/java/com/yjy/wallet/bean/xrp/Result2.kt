package com.yjy.wallet.bean.xrp

data class Result2(val tx_json: TxJson,
                   val engine_result_code: Int = 0,
                   val tx_blob: String = "",
                   val engine_result: String = "",
                   val engine_result_message: String = "",
                   val status: String = "")