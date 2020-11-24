package com.yjy.wallet.bean.htdf

import com.yjy.wallet.chainutils.usdp.AmountItem

data class Fee(val amount: List<AmountItem>?,
               val gas: String = "")