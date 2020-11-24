package com.yjy.wallet.bean.bch

data class BchUnspent(val data: UData,
                      val message: String = "",
                      var status: String = "",
                      var err_code: Int = 0,
                      val err_no: Int = 0)