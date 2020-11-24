package com.yjy.wallet.bean.usdt

/**
 * weiweiyu
 * 2019/7/30
 * 575256725@qq.com
 * 13115284785
 */
class USDTRsp(var address:String,
              var balance: MutableList<USDTBalance> = arrayListOf(),
              var current_page:Int,
              var pages:Int,
              var transactions:MutableList<USDTTx> = arrayListOf())