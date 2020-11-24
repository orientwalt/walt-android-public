package com.yjy.wallet.bean.htdf

data class Tx(val msg: List<MsgItem>?,
              val fee: Fee,
              val memo: String = "",
              val signatures: List<SignaturesItem>?)