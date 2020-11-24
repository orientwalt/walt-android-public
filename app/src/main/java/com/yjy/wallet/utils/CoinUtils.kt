package com.yjy.wallet.utils

import android.content.Context
import android.graphics.drawable.Drawable
import com.ripple.core.coretypes.AccountID
import com.yjy.wallet.R
import com.yjy.wallet.chainutils.neo.NEOSign
import com.yjy.wallet.wallet.ERCType
import com.yjy.wallet.wallet.HTDFERCType
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import org.bitcoinj.core.AddressFormatException
import org.bitcoinj.core.Bech32
import org.stellar.sdk.KeyPair
import org.web3j.abi.datatypes.Address

/**
 * weiweiyu
 * 2019/7/26
 * 575256725@qq.com
 * 13115284785
 */
class CoinUtils {
    companion object {
        fun getDrawableByType(coin: WInfo, context: Context): Drawable {
            return if (coin.unit == WaltType.HET.name) {
                val waltType = WaltType.valueOf(coin.unit)
                context.resources.getDrawable(waltType.drawable)
            } else if (coin.address.startsWith("0x") && coin.unit != WaltType.ETH.name && coin.unit != WaltType.ETC.name) {
                try {
                    val ercType = ERCType.fromAddress(coin.contract_address)
                    context.resources.getDrawable(ercType.drawable)
                } catch (e: java.lang.Exception) {
                    context.resources.getDrawable(R.mipmap.erc_icon)
                }
            } else if (coin.address.startsWith("htdf") && coin.unit != WaltType.htdf.name) {
                try {
                    val ercType = HTDFERCType.fromAddress(coin.contract_address)
                    context.resources.getDrawable(ercType.drawable)
                } catch (e: java.lang.Exception) {
                    context.resources.getDrawable(R.mipmap.hrc_icon)
                }
            } else {
                val waltType = WaltType.valueOf(coin.unit)
                context.resources.getDrawable(waltType.drawable)
            }
        }

        fun getDrawableByType(unit: String, address: String, context: Context): Drawable {
            return if (unit == WaltType.HET.name) {
                val waltType = WaltType.valueOf(unit)
                context.resources.getDrawable(waltType.drawable)
            } else if (address.startsWith("0x") && unit != WaltType.ETH.name && unit != WaltType.ETC.name) {
                val ercType = ERCType.valueOf(unit)
                context.resources.getDrawable(ercType.drawable)
            } else if (address.startsWith("htdf") && unit != WaltType.htdf.name) {
                val hrcType = HTDFERCType.valueOf(unit)
                context.resources.getDrawable(hrcType.drawable)
            } else {
                val waltType = WaltType.valueOf(unit)
                context.resources.getDrawable(waltType.drawable)
            }
        }

        fun checkAddress(type: WaltType, address: String): Boolean {
            val isAddress = true
            when (type) {
                WaltType.htdf, WaltType.usdp, WaltType.HET -> {
                    try {
                        val toData = Bech32.decode(address)
                        when (type) {
                            WaltType.htdf -> {
                                if (toData.hrp != "htdf") {
                                    return false
                                }
                            }
                            WaltType.usdp -> {
                                if (toData.hrp != "usdp") {
                                    return false
                                }
                            }
                            WaltType.HET -> {
                                if (toData.hrp != "0x") {
                                    return false
                                }
                            }
                        }
                    } catch (e: AddressFormatException) {
                        return false
                    }
                }
                WaltType.ETH, WaltType.ETC -> try {
                    if (address.length != 42) {
                        return false
                    }
                    Address(address)
                } catch (e: Exception) {
                    return false
                }
                WaltType.LTC -> {
                    return CoinValidationUtil.bitCoinAddressValidate(address) && address.startsWith("L")
                }
                WaltType.DASH -> {
                    return CoinValidationUtil.bitCoinAddressValidate(address) && address.startsWith("X")
                }
                WaltType.XRP -> {
                    return try {
                        AccountID.fromAddress(address)
                        address.startsWith("r")
                    } catch (e: Exception) {
                        false
                    }
                }
                WaltType.TRX -> {
                    return CoinValidationUtil.bitCoinAddressValidate(address) && address.startsWith("T")
                }
                WaltType.NEO -> {
                    return NEOSign.isValidAddress(address)
                }
                WaltType.QTUM -> {
                    return CoinValidationUtil.bitCoinAddressValidate(address) && (address.startsWith("Q") || address.startsWith("q"))
                }
                WaltType.EOS -> {
                    return isEosAddress(address)
                }
                WaltType.XLM -> {
                    return try {
                        KeyPair.fromAccountId(address).accountId
                        true
                    } catch (e: Exception) {
                        false
                    }
                }
                else -> {//BTC、USDT、BCH、BSV
                    return CoinValidationUtil.bitCoinAddressValidate(address) && address.startsWith("1")
                }
            }
            return isAddress
        }

        fun address2Type(address: String): WaltType? {
            var type: WaltType? = null
            when {
                address.length == 56 && address.startsWith("G") -> {
                    try {
                        KeyPair.fromAccountId(address).accountId
                        type = WaltType.XLM
                    } catch (e: Exception) {

                    }
                }
                address.length <= 12 -> {
                    if (isEosAddress(address))
                        type = WaltType.EOS
                }
                address.startsWith("L") -> {
                    if (CoinValidationUtil.bitCoinAddressValidate(address))
                        type = WaltType.LTC
                }
                address.startsWith("X") -> {
                    if (CoinValidationUtil.bitCoinAddressValidate(address))
                        type = WaltType.DASH
                }
                address.startsWith("1") -> {
                    if (CoinValidationUtil.bitCoinAddressValidate(address)) {
                        type = WaltType.BTC
                    }
                }
                address.startsWith("T") -> {
                    if (CoinValidationUtil.bitCoinAddressValidate(address)) {
                        type = WaltType.TRX
                    }
                }
                address.startsWith("0x") -> {
                    if (address.length == 42) {
                        try {
                            Address(address)
                            type = WaltType.ETH
                        } catch (e: Exception) {

                        }
                    } else {
                        try {
                            val toData = Bech32.decode(address)
                            type = WaltType.HET
                        } catch (e: AddressFormatException) {
                        }
                    }
                }
                address.startsWith("htdf") -> {
                    try {
                        val toData = Bech32.decode(address)
                        type = WaltType.htdf
                    } catch (e: AddressFormatException) {
                    }
                }
                address.startsWith("usdp") -> {
                    try {
                        val toData = Bech32.decode(address)
                        type = WaltType.usdp
                    } catch (e: AddressFormatException) {
                    }
                }
                address.startsWith("r") -> {
                    try {
                        AccountID.fromAddress(address)
                        type = WaltType.XRP
                    } catch (e: Exception) {

                    }
                }
                address.startsWith("A") -> {
                    if (NEOSign.isValidAddress(address)) {
                        type = WaltType.NEO
                    }
                }
                address.startsWith("Q") || address.startsWith("q") -> {
                    if (CoinValidationUtil.bitCoinAddressValidate(address)) {
                        type = WaltType.QTUM
                    }
                }
            }
            return type
        }

        fun isEosAddress(str: String): Boolean {
            val regex = "^[0-5a-z.]{6,12}$"
            return str.matches(regex.toRegex())
        }
    }
}