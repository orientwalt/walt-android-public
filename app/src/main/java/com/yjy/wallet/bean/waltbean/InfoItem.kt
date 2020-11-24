package com.yjy.wallet.bean.waltbean

import java.io.Serializable

data class InfoItem(val price_usd: String = "0",
                    val symbol: String = "",
                    val last_updated: String = "",
                    val total_supply: String = "0",
                    val logo_png: String = "",
                    val price_btc: String = "",
                    val available_supply: String = "",
                    val market_cap_usd: String = "",
                    val percent_change_24h: String = "",
                    val percent_change_1h: String = "",
                    val name: String = "",
                    val volume_24h_usd: String = "",
                    val max_supply: String = "",
                    val rank: String = "",
                    val logo: String = "",
                    val id: String = "",
                    var price_cny: String = "0",
                    val percent_change_7d: String = ""):Serializable