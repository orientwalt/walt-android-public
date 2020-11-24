package com.yjy.wallet.api.unuse

import com.weiyu.baselib.net.ApiManager
import com.yjy.wallet.bean.dash.DashBalance
import com.yjy.wallet.bean.dash.DashTx
import com.yjy.wallet.bean.dash.DashTxs
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

//节点切换不能用单例
class DASHService() {
    val base = "https://insight.dash.org/"
    fun dApi(): DASHApi {
        return ApiManager.instance.getService(base, DASHApi::class.java)
    }

    fun balance(address: String): Flowable<Long> = dApi().balance(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun tx(hax: String): Flowable<DashTx> = dApi().tx(hax).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun txs(address: String): Flowable<DashTxs> = dApi().txs(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun utxo(address: String): Flowable<List<DashBalance>> = dApi().utxo(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

}