package com.yjy.wallet.bean.htdf

import java.io.Serializable

data class PoitInfo(var jailed: Boolean = false,
                    var consensus_pubkey: String = "",
                    var operator_address: String = "",
                    var description: Description,
                    var tokens: Long,
                    var commission: Commission,
                    var unbonding_height: String = "",
                    var delegator_shares: String = "",
                    var unbonding_time: String = "",
                    var min_self_delegation: String = "",
                    var status: Int = 0) : Serializable