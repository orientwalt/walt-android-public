package com.yjy.wallet.bean.xrp

data class TxJson(val account: String = "",
                  val destination: String = "",
                  val transactionType: String = "",
                  val txnSignature: String = "",
                  val signingPubKey: String = "",
                  val amount: Amount,
                  val fee: String = "",
                  val flags: Long = 0,
                  val sequence: Int = 0,
                  val hash: String = "")