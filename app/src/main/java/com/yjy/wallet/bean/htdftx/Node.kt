package com.yjy.wallet.bean.htdftx

data class Node(val node: List<NodeItem> = mutableListOf(),
                var nodetotal: String,
                var nodeprofit: String,
                var nodenum: Int,
                val count: Double = 0.0)