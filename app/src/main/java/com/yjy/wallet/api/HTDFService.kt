package com.yjy.wallet.api

import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.Constant.Companion.mainHTDFNet
import com.yjy.wallet.Constant.Companion.testHtdfIp
import com.yjy.wallet.bean.htdf.AccountInfo
import com.yjy.wallet.bean.htdf.Send
import com.yjy.wallet.bean.htdf.tx.Htdftx
import com.yjy.wallet.bean.params.BroadCast
import com.yjy.wallet.wallet.BitcoinCashBitArrayConverter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.bitcoinj.core.Bech32
import org.web3j.abi.datatypes.Address
import org.web3j.utils.Numeric


class HTDFService() {
    val baseHtdf: String = "http://${if (main) mainHTDFNet else testHtdfIp}:1317"
    fun htdfApi(): HTDFApi {
        return ApiManager.instance.getService(baseHtdf, HTDFApi::class.java)
    }

    fun broadcast(params: BroadCast): Flowable<Send> = htdfApi().broadcast(params).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun getAccount(address: String): Flowable<AccountInfo> = htdfApi().getaccounts(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun gettx(hax: String): Flowable<Htdftx> = htdfApi().gettx(hax).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun getBlockLatest(): Flowable<String> = htdfApi().getBlockLatest().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    fun tokenBalance(contractAddr: String, address: String): Flowable<String> {
        var adddata = Bech32.decode(address).data
        var d = BitcoinCashBitArrayConverter.convertBits(adddata, 5, 8, true)
        val DATA_PREFIX = "70a08231000000000000000000000000${Address(Numeric.toHexString(d)).toString().substring(2)}"
        return htdfApi().getTokenBalance(contractAddr, DATA_PREFIX).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}