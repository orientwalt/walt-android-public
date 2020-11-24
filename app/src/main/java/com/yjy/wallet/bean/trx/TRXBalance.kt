package com.yjy.wallet.bean.trx

data class TRXBalance(val trc20token_balances: List<TrcTokenBalancesItem>?,
                      val address: String = "",
                      val bandwidth: Bandwidth,
                      val accountType: Int = 0,
                      val addressTag: String = "",
                      val frozen: Frozen,
                      val tokenBalances: List<TokenBalancesItem>?,
                      val balance: Long = 0,
                      val voteTotal: Int = 0,
                      val name: String = "",
                      val delegated: Delegated,
                      val totalTransactionCount: Int = 0,
                      val representative: Representative)