package com.yjy.wallet.bean.eos

data class ListItem(val block_time: String = "",
                    val block_num: Int = 0,
                    val data: Data,
                    val name: String = "",
                    val id: String = "",
                    val account: String = "",
                    val global_sequence: String = "",
                    val status: String = "")