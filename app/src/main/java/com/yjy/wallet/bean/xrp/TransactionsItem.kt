package com.yjy.wallet.bean.xrp

data class TransactionsItem(val tx: Tx,
                            val validated: Boolean = false,
                            val meta: Meta)