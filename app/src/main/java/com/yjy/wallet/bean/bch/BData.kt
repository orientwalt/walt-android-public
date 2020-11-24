package com.yjy.wallet.bean.bch

data class BData(val unconfirmed_received: Int = 0,
                 val address: String = "",
                 val tx_count: Int = 0,
                 val balance: Long = 0,
                 val unconfirmed_tx_count: Int = 0,
                 val received: Long = 0,
                 val first_tx: String = "",
                 val unspent_tx_count: Int = 0,
                 val last_tx: String = "",
                 val sent: Long = 0,
                 val unconfirmed_sent: Int = 0)