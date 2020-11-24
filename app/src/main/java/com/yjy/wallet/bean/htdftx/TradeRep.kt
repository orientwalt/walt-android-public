package com.yjy.wallet.bean.htdftx

/**
 *Created by weiweiyu
 *on 2019/5/22
 */
data class TradeRep(var code: Int,
                    var trade: MutableList<Trade> = arrayListOf(),
                    var pagenum: Int)