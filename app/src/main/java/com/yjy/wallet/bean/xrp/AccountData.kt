package com.yjy.wallet.bean.xrp

data class AccountData(val Account: String = "",
                       val OwnerCount: Int = 0,
                       val PreviousTxnLgrSeq: Int = 0,
                       val LedgerEntryType: String = "",
                       val index: String = "",
                       val PreviousTxnID: String = "",
                       val Flags: Int = 0,
                       val Sequence: Int = 0,
                       val Balance: String = "")