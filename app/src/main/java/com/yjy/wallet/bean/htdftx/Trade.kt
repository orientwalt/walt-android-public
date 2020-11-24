package com.yjy.wallet.bean.htdftx

/**
 *Created by weiweiyu
 *on 2019/5/22
 */
data class Trade(
        val money: String = "",
        val memo: String = "",
        val from: String = "",
        val id: Int = 0,
        val tradehash: String = "",
        val to: String = "",
        val type: String = "",
        val blockheight: Long = 0,
        var inval: Int = 0,
        val tradetime: String = "",
        var contract_money: String = "0",
        var contract_address: String = "",
        var transfer_type: Int = 0,
        var contract_type: String = ""//0代表普通交易 1合约创建 2合约查询 3合约转帐 4委托 5收益转余额
)