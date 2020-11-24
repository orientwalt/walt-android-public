package com.yjy.wallet.bean.eth

import com.yjy.wallet.bean.htdf.Result

data class ETHTxCheck(val result: Result,
                      val message: String = "",
                      val status: String = "")