package com.yjy.wallet.bean.htdf

data class Transactions(val FromHeight: String = "",
                        val ArrTx: List<ArrResultMsgItem>?,
                        val ChainHeight: String = "",
                        val EndHeight: String = "")