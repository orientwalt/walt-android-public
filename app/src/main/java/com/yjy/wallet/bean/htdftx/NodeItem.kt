package com.yjy.wallet.bean.htdftx

import java.io.Serializable

data class NodeItem(val server_name: String = "",
                    var server_logo: String = "",
                    val proportion: Double = 0.0,
                    val server_ip: String = "",
                    val id: Int = 0,
                    val server_introduce: String = "",
                    val server_address: String = "",
                    val node_num: Int = 0,
                    var ranking: Int = 1,
                    var rank: Int = 1,
                    var identification: Int,
                    val node_total: Double = 0.0) : Serializable