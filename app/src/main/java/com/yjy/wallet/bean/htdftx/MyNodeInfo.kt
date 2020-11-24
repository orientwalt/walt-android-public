package com.yjy.wallet.bean.htdftx

import java.io.Serializable

data class MyNodeInfo(val shares: String = "",
                      val profit: String = "",
                      val status: Boolean
) : Serializable