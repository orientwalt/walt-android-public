package com.yjy.wallet.bean.bch

data class UData(val total_count: Int = 0,
                 val page_size: Int = 0,
                 var page_total: Int = 0,
                 var page: Int = 0,
                 val list: MutableList<UtxoItem> = arrayListOf())