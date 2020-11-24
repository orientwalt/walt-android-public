package com.yjy.wallet.bean.waltbean

data class ListItem(val createtime: String = "",
                    val amount: String = "",
                    val type: Int = 0,
                    val expireTime:String="",
                    val coin: String = "")