package com.yjy.wallet.utils

import com.google.common.base.Splitter
import com.ripple.core.coretypes.AccountID
import com.weiyu.baselib.tinker.util.BaseApplicationContext
import com.weiyu.baselib.util.AesUtils
import com.yjy.wallet.Constant
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.chainutils.dash.DashUtils
import com.yjy.wallet.chainutils.ltc.LTCUtils
import com.yjy.wallet.chainutils.neo.NEOSign
import com.yjy.wallet.chainutils.qtum.QtumMainNetParams
import com.yjy.wallet.chainutils.qtum.QtumTestNetParams
import com.yjy.wallet.chainutils.trx.TRXUtils
import com.yjy.wallet.chainutils.xlm.StrKey
import com.yjy.wallet.wallet.BitcoinCashBitArrayConverter
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import io.github.novacrypto.bip39.MnemonicGenerator
import io.github.novacrypto.bip39.WordList
import io.github.novacrypto.bip39.Words
import org.bitcoinj.core.*
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.HDUtils
import org.bitcoinj.crypto.MnemonicException.MnemonicLengthException
import org.bitcoinj.script.Script
import org.bitcoinj.wallet.DeterministicSeed
import org.bitcoinj.wallet.Wallet
import org.stellar.sdk.KeyPair
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import org.web3j.utils.Numeric
import java.security.SecureRandom
import java.util.*


class YJYWalletUtils {
    companion object {
        var basePath = BaseApplicationContext.context.cacheDir.absolutePath
        fun getMnemoics(wordList: WordList): String {
            var sb = StringBuilder()
            val entropy = ByteArray(Words.TWELVE.byteLength())
            Words.TWELVE.byteLength()
            SecureRandom().nextBytes(entropy)
            MnemonicGenerator(wordList).createMnemonic(entropy) { sb.append(it) }
            return sb.toString()
        }

        //创建钱包
        fun createWallet(wordList: WordList): Wallet {
            Context(Constant().currentNetworkParams)
            var wallet: Wallet?
            //password为输入的钱包密码
            var seed: DeterministicSeed? = null
            try {
                seed = DeterministicSeed(Splitter.on(" ").splitToList(getMnemoics(wordList)), null, "", Utils.currentTimeSeconds())
            } catch (e: MnemonicLengthException) {
                e.printStackTrace()
            }
            wallet = Wallet.fromSeed(Constant().currentNetworkParams, seed, Script.ScriptType.P2PKH)
            return wallet
        }

        fun importWallet(mnemonics: String): Wallet {
            Context(Constant().currentNetworkParams)
            var wallet: Wallet?
            //password为输入的钱包密码
            var seed: DeterministicSeed? = null
            try {
                seed = DeterministicSeed(Splitter.on(" ").splitToList(mnemonics), null, "", Utils.currentTimeSeconds())
            } catch (e: MnemonicLengthException) {
                e.printStackTrace()
            }
            wallet = Wallet.fromSeed(Constant().currentNetworkParams, seed, Script.ScriptType.P2PKH)
            return wallet
        }


        fun getWaltByWallet(wallet: Wallet, waltType: WaltType, pwd: String, index: Int): WInfo {
            return getWaltByWallet(wallet, waltType, pwd, index, false)
        }

//        fun getWaltByPath(wallet: Wallet): WInfo {
//            val privateKey = wallet.activeKeyChain.getKeyByPath(HDUtils.parsePath("M/44H/145H/0H/0/0"), true)
//            BLog.d("---------------------------${Numeric.toHexString(privateKey.privKeyBytes)}")
//            var w1 = getWaltByEcKey(WaltType.htdf, privateKey, "123456")
//            BLog.d("------------------------${w1.address}")
//        }

        fun getWaltByWallet(wallet: Wallet, waltType: WaltType, pwd: String, index: Int, old: Boolean): WInfo {
            var path = when (waltType) {
                WaltType.htdf -> if (old) "145" else "346"
                WaltType.usdp -> if (old) "145" else "345"
                WaltType.ETH -> "60"
                WaltType.ETC -> "61"
                WaltType.BTC -> if (main) "0" else "1"
                WaltType.USDT -> if (main) "0" else "1"
                WaltType.HET -> "368"
                WaltType.BSV -> "236"
                WaltType.BCH -> "145"
                WaltType.LTC -> if (main) "2" else "1"
                WaltType.DASH -> if (main) "5" else "1"
                WaltType.XRP -> "144"
                WaltType.TRX -> "195"
                WaltType.NEO -> "888"
                WaltType.CXC -> if (main) "0" else "1"//xcx没有路径
                WaltType.QTUM -> "2301"
                WaltType.EOS -> "194"
                WaltType.XLM -> "148"
            }
            var privateKey = when (waltType) {
                WaltType.CXC -> {
                    HDKeyDerivation.createMasterPrivateKey(wallet.keyChainSeed.seedBytes)
                }
                WaltType.QTUM -> {
                    val pathParent = ArrayList<ChildNumber>()
                    pathParent.add(ChildNumber(88, true))
                    pathParent.add(ChildNumber(0, true))
                    val qpath = HDUtils.append(pathParent, ChildNumber(0, true))
                    wallet.activeKeyChain.getKeyByPath(qpath, true)
                }
                WaltType.XLM -> {
                    var key = StrKey.decodeStellarSecretSeed(KeyPair.fromBip39Seed(wallet.activeKeyChain.seed?.seedBytes, 0).secretSeed)
                    ECKey.fromPrivate(key)
                }
                else -> {
                    wallet.activeKeyChain.getKeyByPath(HDUtils.parsePath("M/44H/${path}H/0H/0/$index"), true)
                }
            }
            return getWaltByEcKey(waltType, privateKey, pwd)
        }

        fun getWaltByKey(waltType: WaltType, key: ByteArray, pwd: String): WInfo {
            var ecKey = ECKey.fromPrivate(key)
            return getWaltByEcKey(waltType, ecKey, pwd)
        }

        fun getWaltByEcKey(waltType: WaltType, ecKey: ECKey, pwd: String): WInfo {
            var address = ""
            when (waltType) {
                WaltType.htdf -> {
                    val payloadBytes2 = BitcoinCashBitArrayConverter.convertBits(ecKey.pubKeyHash, 8, 5, true)
                    address = Bech32.encode("htdf", payloadBytes2)
                }
                WaltType.usdp -> {
                    val payloadBytes2 = BitcoinCashBitArrayConverter.convertBits(ecKey.pubKeyHash, 8, 5, true)
                    address = Bech32.encode("usdp", payloadBytes2)
                }
                WaltType.HET -> {
                    val payloadBytes2 = BitcoinCashBitArrayConverter.convertBits(ecKey.pubKeyHash, 8, 5, true)
                    address = Bech32.encode("0x", payloadBytes2)
                }
                WaltType.ETH, WaltType.ETC -> {
                    val ecKeyPair = ECKeyPair.create(ecKey.privKey)
                    address = "0x" + Keys.getAddress(ecKeyPair)
                }
                WaltType.BTC, WaltType.USDT
                    , WaltType.CXC
                -> {
                    address = LegacyAddress.fromKey(Constant().currentNetworkParams, ecKey).toString()
                }
                WaltType.BCH, WaltType.BSV -> {
                    address = LegacyAddress.fromKey(Constant().currentNetworkParams, ecKey).toString()
//                    val b = LegacyAddress.fromKey(Constant().currentNetworkParams, ecKey).toString()
//                    address = AddressConverter.toCashAddress(b)//BCH类型地址
                }
                WaltType.LTC -> {
                    address = LTCUtils.getAddress(ecKey.pubKeyHash)
                }
                WaltType.DASH -> {
                    address = DashUtils.getAddress(ecKey.pubKeyHash)
                }
                WaltType.XRP -> {
                    address = AccountID.fromBytes(ecKey.pubKeyHash).toString()
                }
                WaltType.NEO -> {
                    var ecKeyPair = com.yjy.wallet.chainutils.neo.ECKeyPair.create(ecKey.privKeyBytes)
                    address = NEOSign.getAddress(ecKeyPair.publicKey)
                }
                WaltType.TRX -> {
                    address = TRXUtils.getAddress(ecKey.privKey)
                }
                WaltType.QTUM -> {
                    address = if (main)
                        LegacyAddress.fromKey(QtumMainNetParams.get(), ecKey).toString()
                    else
                        LegacyAddress.fromKey(QtumTestNetParams.get(), ecKey).toString()
                }
                WaltType.EOS -> {
                    address = ""
                }
                WaltType.XLM -> {
                    address = KeyPair.fromSecretSeed(ecKey.privKeyBytes).accountId
                }
            }
//            var walletFile = KeyStoneUtil.createWalletFile(pwd, ECKeyPair.create(ecKey.privKey), false, address)
            val keyString = AesUtils.encrypt(Numeric.toHexStringNoPrefix(ecKey.privKey), pwd)
            var winfo = WInfo("", keyString, address, "", "", "0", waltType.name)
            winfo.old = false
            return winfo
        }
    }
}