package com.yjy.wallet.api.unuse

import android.annotation.SuppressLint
import com.weiyu.baselib.net.ApiManager
import com.weiyu.baselib.util.BLog
import com.yjy.wallet.Constant
import com.yjy.wallet.bean.eth.*
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.core.methods.response.EthCall
import org.web3j.protocol.core.methods.response.EthGetCode
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.core.methods.response.EthTransaction
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger

/**
 *Created by weiweiyu
 *on 2019/5/31
 * ETH钱包查询转账
 */
class ETHService {
    var apikey = "9K6YBDTGVGFTEYQZ41F5E2CU42QCFEBIDJ"
    //https://api.etherscan.io
    //https://ropsten.etherscan.io
    //https://rinkeby.etherscan.io
    val base: String = if (Constant.main) "http://api-cn.etherscan.com" else "https://ropsten.etherscan.io"

    fun eTHApi(): ETHApi {
        return ApiManager.instance.getService(base, ETHApi::class.java)
    }

    fun getBalance(address: String): Flowable<ETHBalance> {
        val map: MutableMap<String, String> = mutableMapOf()
        map["module"] = "account"
        map["action"] = "balance"
        map["address"] = address
        map["tag"] = "latest"
        map["apikey"] = apikey
        return eTHApi().balance(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getTokenBalance(address: String, contractaddress: String): Flowable<ETHBalance> {
        var map: MutableMap<String, String> = mutableMapOf()
        map["module"] = "account"
        map["action"] = "tokenbalance"
        map["contractaddress"] = contractaddress
        map["address"] = address
        map["tag"] = "latest"
        map["apikey"] = apikey
        return eTHApi().tokenBalance(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun tokensupply(contractaddress: String): Flowable<ETHBalance> {
        var map: MutableMap<String, String> = mutableMapOf()
        map["module"] = "stats"
        map["action"] = "tokensupply"
        map["contractaddress"] = contractaddress
        map["apikey"] = apikey
        return eTHApi().tokensupply(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun check(hax: String): Flowable<ETHTxCheck> {
        val map: MutableMap<String, String> = mutableMapOf()
        map["module"] = "transaction"
        map["action"] = "gettxreceiptstatus"
        map["txhash"] = hax
        map["apikey"] = apikey
        return eTHApi().check(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun checkToken(hax: String): Flowable<ETHTokenCheck> {
        val map: MutableMap<String, String> = mutableMapOf()
        map["module"] = "transaction"
        map["action"] = "getstatus"
        map["txhash"] = hax
        map["apikey"] = apikey
        return eTHApi().checktoken(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getTxs(address: String, page: Int): Flowable<ETHTx> {
        val map: MutableMap<String, String> = mutableMapOf()
        map["module"] = "account"
        map["action"] = "txlist"
        map["address"] = address
        map["startblock"] = "1"
        map["endblock"] = "99999999"
        map["page"] = page.toString()
        map["offset"] = "50"
        map["sort"] = "desc"
        map["apikey"] = apikey
        return eTHApi().getTxs(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getTokenTx(address: String, page: String, contractaddress: String): Flowable<TokenTx> {
        var map: MutableMap<String, String> = mutableMapOf()
        map["module"] = "account"
        map["action"] = "tokentx"
        map["contractaddress"] = contractaddress
        map["address"] = address
        map["page"] = page
        map["offset"] = "50"
        map["sort"] = "desc"
        map["apikey"] = apikey
        return eTHApi().tokenTx(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getPrice(): Flowable<ETHGas> {
        var map: MutableMap<String, String> = mutableMapOf()
        map["module"] = "gastracker"
        map["action"] = "gasoracle"
        map["apikey"] = apikey
        return eTHApi().gasoracle(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getTxResult(txhash: String): Flowable<EthTransaction> {
        var map: MutableMap<String, String> = mutableMapOf()
        map["module"] = "proxy"
        map["action"] = "eth_getTransactionByHash"
        map["txhash"] = txhash
        map["apikey"] = apikey
        return eTHApi().gettx(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getCount(address: String): Flowable<EthGetCode> {
        var map: MutableMap<String, String> = mutableMapOf()
        map["module"] = "proxy"
        map["action"] = "eth_getTransactionCount"
        map["address"] = address
        map["tag"] = "latest"
        map["apikey"] = apikey
        return eTHApi().getCode(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun send(hex: String): Flowable<EthSendTransaction> {
        var map: MutableMap<String, String> = mutableMapOf()
        map["module"] = "proxy"
        map["action"] = "eth_sendRawTransaction"
        map["hex"] = hex
        map["apikey"] = apikey
        return eTHApi().send(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun eth_call(address: String, hex: String): Flowable<EthCall> {
        var map: MutableMap<String, String> = mutableMapOf()
        map["module"] = "proxy"
        map["action"] = "eth_call"
        map["to"] = address
        map["data"] = hex
        map["tag"] = "latest"
        map["apikey"] = apikey
        return eTHApi().eth_call(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    //to地址,金额,from私钥
    @SuppressLint("CheckResult")
    fun send(toAddress: String, amount: Double, key: Credentials, fee: Int, memo: String, limit: BigInteger, n: BigInteger?): Flowable<EthSendTransaction> {
        return Flowable.just(key)
                .flatMap {
                    getCount(key.address)
                }
                .flatMap {
                    var gasprice = Convert.toWei(fee.toString(), Convert.Unit.GWEI).toBigInteger()
                    BLog.d("_____________$gasprice")
                    val value = Convert.toWei(amount.toString(), Convert.Unit.ETHER).toBigInteger()
                    val nonce = n ?: Numeric.decodeQuantity(it.result)
                    val rawTransaction = RawTransaction.createTransaction(
                            nonce, gasprice,
                            limit, toAddress,
                            value, memo)
                    val signedMessage = TransactionEncoder.signMessage(rawTransaction, key)
                    val hexValue = Numeric.toHexString(signedMessage)
                    send(hexValue)
                }
    }

    //to地址,金额,from私钥
    @SuppressLint("CheckResult")
    fun sendERC(toAddress: String, amount: Double, key: Credentials, fee: Int, contractaddress: String, decimal: Int, limit: BigInteger, n: BigInteger?): Flowable<EthSendTransaction> {
        return Flowable.just("")
                .flatMap {
                    getCount(key.address)
                }
                .flatMap {
                    var gasprice = Convert.toWei(fee.toString(), Convert.Unit.GWEI).toBigInteger()
                    BLog.d("_____________$gasprice")
                    val value = getValue(amount, decimal)
                    val function = Function(
                            "transfer",
                            listOf(Address(toAddress), Uint256(value)),
                            emptyList())
                    //创建RawTransaction交易对象
                    var encodedFunction = FunctionEncoder.encode(function)
                    val nonce = n ?: Numeric.decodeQuantity(it.result)
                    val rawTransaction = RawTransaction.createTransaction(nonce, gasprice, limit, contractaddress, encodedFunction)
                    val signedMessage = TransactionEncoder.signMessage(rawTransaction, key)
                    val hexValue = Numeric.toHexString(signedMessage)
                    send(hexValue)
                }
    }


    fun getValue(amount: Double, decimal: Int): BigInteger {
        return if (decimal <= 6) {
            var s = BigDecimal.TEN.pow(decimal).toDouble().times(amount).toBigDecimal().setScale(0, BigDecimal.ROUND_HALF_UP).toBigInteger()
            s
        } else {
            var s = BigDecimal(amount.times(1000000).toString()).setScale(0, BigDecimal.ROUND_HALF_UP).toBigInteger()
            var n = BigDecimal.TEN.pow(decimal - 6).toBigInteger()
            s.times(n)
        }
    }
}