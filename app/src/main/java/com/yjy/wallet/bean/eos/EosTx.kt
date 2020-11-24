package com.yjy.wallet.bean.eos

data class EosTx(val block_time: String = "",
                 val elapsed: Int = 0,
                 val block_num: String = "",
                 val producer_block_id: String = "",
                 val scheduled: Boolean = false,
                 val receipt: Receipt,
                 val id: String = "",
                 val net_usage: Int = 0)