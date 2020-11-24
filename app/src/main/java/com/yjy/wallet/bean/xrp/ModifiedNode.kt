package com.yjy.wallet.bean.xrp

data class ModifiedNode(val ledgerIndex: String = "",
                        val finalFields: FinalFields,
                        val ledgerEntryType: String = "",
                        val previousFields: PreviousFields1,
                        val previousTxnLgrSeq: Int = 0,
                        val previousTxnID: String = "")