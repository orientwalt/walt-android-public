package com.yjy.wallet.bean.eos

data class TotalResources(val owner: String = "",
                          val ramBytes: Int = 0,
                          val netWeight: String = "",
                          val cpuWeight: String = "")