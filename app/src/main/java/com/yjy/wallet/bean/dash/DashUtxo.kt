package com.yjy.wallet.bean.dash

data class DashUtxo(val tx_input_n: Long = 0,
                    val spent: Boolean = false,
                    val tx_output_n: Long = 0,
                    val tx_hash: String = "",
                    val double_spend: Boolean = false,
                    val block_height: Int = 0,
                    val confirmations: Long = 0,
                    val value: Long = 0,
                    val ref_balance: Long = 0,
                    val script: String = "",
                    val confirmed: String = "")