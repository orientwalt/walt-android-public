package com.yjy.wallet.bean.eos

data class EosAccount(val head_blockNum: Int = 0,
                      val total_resources: TotalResources,
                      val head_block_time: String = "",
                      val created: String = "",
                      val ram_quota: Int = 0,
                      val net_limit: NetLimit,
                      val core_liquid_balance: String = "",
                      val net_weight: Int = 0,
                      val cpu_weight: Int = 0,
                      val privileged: Boolean = false,
                      val ram_usage: Int = 0,
                      val account_name: String = "",
                      val cpu_limit: CpuLimit)