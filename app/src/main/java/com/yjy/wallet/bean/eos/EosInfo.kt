package com.yjy.wallet.bean.eos

data class EosInfo(val head_block_num: Int = 0,
                   val fork_db_head_block_num: Int = 0,
                   val chain_id: String = "",
                   val head_block_time: String = "",
                   val virtual_block_net_limit: Int = 0,
                   val virtual_block_cpu_limit: Int = 0,
                   val last_irreversible_block_num: Long = 0,
                   val server_version: String = "",
                   val block_cpu_limit: Int = 0,
                   val head_block_producer: String = "",
                   val fork_db_head_block_id: String = "",
                   val last_irreversible_block_id: String = "",
                   val block_net_limit: Int = 0,
                   val head_block_id: String = "",
                   val server_version_string: String = "")