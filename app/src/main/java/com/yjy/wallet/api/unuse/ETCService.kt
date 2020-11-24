package com.yjy.wallet.api.unuse

import android.annotation.SuppressLint
import com.google.gson.JsonObject
import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.bean.etc.*
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.math.BigInteger

/**
 *Created by weiweiyu
 *on 2019/5/31
 * ETH钱包查询转账
 */
class ETCService {
    val base: String = "https://api.cryptoapis.io/"

    fun eTHApi(): ETCApi {
        return ApiManager.instance.getService(base, ETCApi::class.java)
    }

    fun getBalance(address: String): Flowable<ETCBase<ETCBalance>> {
        return eTHApi().balance(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getfee(): Flowable<ETCBase<ETCFee>> {
        return eTHApi().fee().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun tx(hax: String): Flowable<ETCBase<ETCTx>> {
        return eTHApi().tx(hax).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }


    fun getTxs(address: String): Flowable<ETCBase<List<ETCTxsItem>>> {
        return eTHApi().getTxs(address, "0", "15").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun push(tx: String): Flowable<ETCBase<ETCSend>> {
        var json = JsonObject()
        json.addProperty("hex", tx)
        return eTHApi().push(json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    //to地址,金额,from私钥
    @SuppressLint("CheckResult")
    fun send(toAddress: String, amount: Double, key: Credentials, fee: BigInteger, memo: String, limit: BigInteger): Flowable<ETCBase<ETCSend>> {
        var info: ETCBalance? = null
        return Flowable.just(toAddress)
                .map {
                    val value = Convert.toWei(amount.toString(), Convert.Unit.ETHER).toBigInteger()
                    val rawTransaction = RawTransaction.createTransaction(
                            info!!.txs_count.toBigInteger(), fee,
                            limit, toAddress,
                            value, memo)
                    val signedMessage = TransactionEncoder.signMessage(rawTransaction, key)
                    val hexValue = Numeric.toHexString(signedMessage)
                    hexValue
                }
                .flatMap {
                    push(it)
                }
    }

}