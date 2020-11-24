package com.yjy.wallet.bean.trx

data class TRXTxs(val wholeChainTxCount: Int = 0,
                  val total: Int = 0,
                  val data: List<TData>?,
                  val rangeTotal: Int = 0)