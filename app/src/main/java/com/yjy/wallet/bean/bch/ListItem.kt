package com.yjy.wallet.bean.bch

data class ListItem(
        val outputs: List<OutputsItem>?,
        val vsize: Int = 0,
        val inputs: List<InputsItem>?,
        val fee: Long = 0,
        val block_hash: String = "",
        val weight: Int = 0,
        val block_height: Int = 0,
        val confirmations: Int = 0,
        val version: Int = 0,
        val is_double_spend: Boolean = false,
        val created_at: Long = 0,
        val size: Int = 0,
        val sigops: Int = 0,
        val is_coinbase: Boolean = false,
        val hash: String = "")