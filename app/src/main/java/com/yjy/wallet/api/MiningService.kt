package com.yjy.wallet.api

import com.weiyu.baselib.net.ApiManager
import com.weiyu.baselib.net.BaseResult
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.bean.HtdfApp
import com.yjy.wallet.bean.htdf.Send
import com.yjy.wallet.bean.htdftx.MyNodeInfo
import com.yjy.wallet.bean.htdftx.Node
import com.yjy.wallet.bean.htdftx.Trade
import com.yjy.wallet.bean.waltbean.AppInfo
import com.yjy.wallet.bean.waltbean.UnWeituoLog
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

//节点切换不能用单例
class MiningService() {
    val base: String = if (main) "" else "http://uat1-system-htdf.orientwalt.com"

    fun txApi(): MiningApi {
        return ApiManager.instance.getService(base, MiningApi::class.java)
    }

    fun getserver(page: Int, pageSize: Int, type: Int, name: String, addr: String): Flowable<BaseResult<Node>> {
        var map: MutableMap<String, Any> = mutableMapOf()
        map["page"] = page
        map["limit"] = pageSize
        map["type"] = type
        map["name"] = name
        map["address"] = addr
        return txApi().getserver(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun myServer(addr: String): Flowable<BaseResult<Node>> {
        return txApi().myserver(addr).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun nodedelegation(tx: String, addr: String, price: String, id: String): Flowable<BaseResult<Send>> {
        return txApi().nodedelegation(tx, addr, price, id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun relievenode(addr: String, id: String, total: String, type: String): Flowable<BaseResult<String>> = txApi().relievenode(addr, id, total, type).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun getinfo(daddress: String, vaddress: String): Flowable<BaseResult<MyNodeInfo>> = txApi().getinfo(vaddress, daddress).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun check(daddress: String, vaddress: String): Flowable<BaseResult<Boolean>> = txApi().check(vaddress, daddress).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun relievelist(vaddress: String, daddress: String): Flowable<BaseResult<List<HtdfApp>>> = txApi().relievelist(vaddress, daddress).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun history_off_detail(vaddress: String, daddress: String, page: String, limit: String): Flowable<BaseResult<List<Trade>>> =
            txApi().history_off_detail(vaddress, daddress, page, limit).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun history_detail(vaddress: String, daddress: String, page: String, limit: String): Flowable<BaseResult<UnWeituoLog>> =
            txApi().history_detail(vaddress, daddress, page, limit).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun extract(id: String, vaddress: String, daddress: String, tx: String): Flowable<BaseResult<String>> {
        var map: MutableMap<String, Any> = mutableMapOf()
        map["id"] = id
        map["delegator_address"] = daddress
        map["validator_address"] = vaddress
        map["txhash"] = tx
        return txApi().extract(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun delegatortotal(vaddress: String, daddress: String): Flowable<BaseResult<String>> {
        var map: MutableMap<String, Any> = mutableMapOf()
        map["delegator_address"] = daddress
        map["validator_address"] = vaddress
        return txApi().delegatortotal(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun extract_detail(id: String): Flowable<BaseResult<AppInfo>> {
        return txApi().extract_detail(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

}