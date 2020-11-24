package com.yjy.wallet.bean.xrp

data class Tx(
        var Amount: String,
        val date: Long = 0,
        val Account: String = "",
        var Destination: String = "",
        val TransactionType: String = "",
        val ledger_index: Long = 0,
        val SigningPubKey: String = "",
        val Fee: String = "",
        val takerGets: String = "",
        val flags: Long = 0,
        val sequence: Int = 0,
        val LastLedgerSequence: Int = 0,
        val takerPays: TakerPays,
        val txnSignature: String = "",
        val inLedger: Int = 0,
        val hash: String = "")