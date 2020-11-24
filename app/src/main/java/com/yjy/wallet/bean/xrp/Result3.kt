package com.yjy.wallet.bean.xrp

data class Result3(val date: Int = 0,
                   val Account: String = "",
                   var Amount: String = "",
                   var Destination: String = "",
                   val TransactionType: String = "",
                   val ledger_index: Int = 0,
                   val limitAmount: LimitAmount1,
                   val SigningPubKey: String = "",
                   val Fee: String = "",
                   val Flags: Long = 0,
                   val Sequence: Int = 0,
                   val LastLedgerSequence: Int = 0,
                   val TxnSignature: String = "",
                   val validated: Boolean = false,
                   val meta: Meta,
                   val inLedger: Int = 0,
                   val hash: String = "")