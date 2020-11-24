package com.yjy.wallet.bean.eth

data class Result(val isError: String = "",
                  val errDescription: String = "",
                  val LastBlock: String,
                  val SafeGasPrice: String,
                  val ProposeGasPrice: String)