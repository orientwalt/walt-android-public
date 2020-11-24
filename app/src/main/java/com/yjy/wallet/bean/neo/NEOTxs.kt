package com.yjy.wallet.bean.neo

data class NEOTxs(val entries: List<EntriesItem>?,
                  val page_number: Int = 0,
                  val total_pages: Int = 0,
                  val total_entries: Int = 0,
                  val page_size: Int = 0)