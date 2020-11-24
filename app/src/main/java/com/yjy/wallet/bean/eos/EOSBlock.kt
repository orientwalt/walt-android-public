package com.yjy.wallet.bean.eos

data class EOSBlock(val ref_block_prefix: Long = 0,
                    val previous: String = "",
                    val schedule_version: Int = 0,
                    val producer_signature: String = "",
                    val confirmed: Int = 0,
                    val block_num: Int = 0,
                    val producer: String = "",
                    val transaction_mroot: String = "",
                    val id: String = "",
                    val action_mroot: String = "",
                    val timestamp: String = "")