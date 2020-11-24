package com.yjy.wallet.bean.trx

data class TData(val contractRet: String = "",
                 val cost: Cost,
                 val data: String = "",
                 val contractType: Int = 0,
                 val fee: String = "",
                 val toAddress: String = "",
                 val confirmed: Boolean = false,
                 val events: String = "",
                 val smartCalls: String = "",
                 val block: Long = 0,
                 val ownerAddress: String = "",
                 val id: String = "",
                 val hash: String = "",
                 val contractData: TContract,
                 val timestamp: Long = 0)