package com.yjy.wallet.api

import com.yjy.wallet.Constant.Companion.main
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.stellar.sdk.Server
import org.stellar.sdk.Transaction
import org.stellar.sdk.responses.*

/**
 * weiweiyu
 * 2020/6/8
 * 575256725@qq.com
 * 13115284785
 */
class XLMService {
    var sever = Server(if (main) "https://horizon.stellar.org" else "https://horizon-testnet.stellar.org")

    fun getAccounts(account: String): Flowable<AccountResponse> {
        return Flowable.just(sever)
                .map {
                    var a = it.accounts().account(account)
                    a
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getTxs(account: String): Flowable<Page<TransactionResponse>> {
        return Flowable.just(sever)
                .map {
                    it.transactions().forAccount(account).limit(30).execute()
                }
    }

    fun getTx(hex: String): Flowable<TransactionResponse> {
        return Flowable.just(sever)
                .map {
                    it.transactions().transaction(hex)
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun send(transaction: Transaction): Flowable<SubmitTransactionResponse> {
        return Flowable.just(sever)
                .map {
                    it.submitTransaction(transaction)
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getFee(): Flowable<FeeStatsResponse> {
        return Flowable.just(sever)
                .map {
                    it.feeStats().execute()
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}