package com.yjy.wallet.bean.bch

data class BchTxs(val data: TData,
                  val errMsg: String = "",
                  val errNo: Int = 0)