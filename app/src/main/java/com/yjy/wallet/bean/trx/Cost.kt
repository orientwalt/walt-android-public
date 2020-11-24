package com.yjy.wallet.bean.trx

data class Cost(val netFee: Int = 0,
                val energyUsage: Int = 0,
                val energyFee: Int = 0,
                val energyUsageTotal: Int = 0,
                val originEnergyUsage: Int = 0,
                val netUsage: Int = 0)