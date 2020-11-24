package com.yjy.wallet.api


/**
 * weiweiyu
 * 2019/7/29
 * 575256725@qq.com
 * 13115284785
 */
class EOSService() {

//    var key = "03fa55cb-e11d-46a2-ae6e-0f095a889f24"
//    //https://www.blockdog.com/  https://www.dfuse.io/  https://eospark.com/
//    var base = "https://open-api.eos.blockdog.com"
//
//    fun eosApi(): EOSApi {
//        return ApiManager.instance.getService(base, EOSApi::class.java)
//    }
//
//    fun get_key_accounts(pKey: String): Flowable<EosAccounts> {
//        var json = JsonObject()
//        json.addProperty("public_key", pKey)
//        return eosApi().get_key_accounts(key, json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//    }
//
//    fun get_transaction(id: String): Flowable<EosTx> {
//        var json = JsonObject()
//        json.addProperty("id", id)
//        return eosApi().get_transaction(key, json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//    }
//
//    fun get_account(account_name: String): Flowable<EosAccount> {
//        var json = JsonObject()
//        json.addProperty("account_name", account_name)
//        return eosApi().get_account(key, json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//    }
//
//    fun get_currency_balance(account: String): Flowable<List<String>> {
//        var json = JsonObject()
//        json.addProperty("code", "eosio.token")
//        json.addProperty("account", account)
//        json.addProperty("smybol", "EOS")
//        return eosApi().get_currency_balance(key, json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//    }
//
//    fun get_account_transfer(account: String, start: Int): Flowable<EosTxs> {
//        var json = JsonObject()
//        json.addProperty("code", "eosio.token")
//        json.addProperty("account_name", account)
//        json.addProperty("smybol", "EOS")
//        json.addProperty("start_block_num", start)
//        json.addProperty("end_block_num", 999999999)
//        return eosApi().get_account_transfer(key, json).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//    }
//
//
//    fun send(key1: String, from: String, to: String, price: String, memo: String): Flowable<EosSend> {
//
//        var e: EosInfo? = null
//        return eosApi().get_info(key)
//                .flatMap {
//                    e = it
//                    var j = JsonObject()
//                    j.addProperty("block_num_or_id", it.last_irreversible_block_id)
//                    eosApi().get_block(key, j)
//                }.map {
//                    var price1 = Utils.toSubStringDegistForChart(price.toDouble(), 4, true)
//                    var content = EosUtils.transfer(key1, "eosio.token",
//                            from, to, "$price1 EOS", memo, 60, e!!.chain_id, e!!.head_block_time, e!!.last_irreversible_block_id)
//                    content
//                }.flatMap {
//                    eosApi().push_transaction(key, it)
//                }
//    }
//
//    fun delegatebw(key1: String, from: String, to: String, price: String, pricenet: String): Flowable<EosSend> {
//        var e: EosInfo? = null
//        return eosApi().get_info(key)
//                .flatMap {
//                    e = it
//                    var j = JsonObject()
//                    j.addProperty("block_num_or_id", it.last_irreversible_block_id)
//                    eosApi().get_block(key, j)
//                }.map {
//                    var price1 = Utils.toSubStringDegistForChart(price.toDouble(), 4, true)
//                    var price2 = Utils.toSubStringDegistForChart(pricenet.toDouble(), 4, true)
//                    var content = EosUtils.delegatebw(key1, from, to, "$price1 EOS", "$price2 EOS", 60, e!!.chain_id, e!!.head_block_time, e!!.last_irreversible_block_id)
//                    content
//                }.flatMap {
//                    eosApi().push_transaction(key, it)
//                }
//    }
//
//    fun undelegatebw(key1: String, from: String, to: String, price: String, netprice: String): Flowable<EosSend> {
//        var e: EosInfo? = null
//        return eosApi().get_info(key)
//                .flatMap {
//                    e = it
//                    var j = JsonObject()
//                    j.addProperty("block_num_or_id", it.last_irreversible_block_id)
//                    eosApi().get_block(key, j)
//                }.map {
//                    var price1 = Utils.toSubStringDegistForChart(price.toDouble(), 4, true)
//                    var price2 = Utils.toSubStringDegistForChart(netprice.toDouble(), 4, true)
//                    var content = EosUtils.undelegatebw(key1, from, to, "$price1 EOS", "$price2 EOS", 60, e!!.chain_id, e!!.head_block_time, e!!.last_irreversible_block_id)
//                    content
//                }.flatMap {
//                    eosApi().push_transaction(key, it)
//                }
//    }
//
//    fun buyram(key1: String, from: String, price: String): Flowable<EosSend> {
//        var e: EosInfo? = null
//        return eosApi().get_info(key)
//                .flatMap {
//                    e = it
//                    var j = JsonObject()
//                    j.addProperty("block_num_or_id", it.last_irreversible_block_id)
//                    eosApi().get_block(key, j)
//                }.map {
//                    var price1 = Utils.toSubStringDegistForChart(price.toDouble(), 4, true)
//                    var content = EosUtils.buyram(key1, from, "$price1 EOS", 60, e!!.chain_id, e!!.head_block_time, e!!.last_irreversible_block_id)
//                    content
//                }.flatMap {
//                    eosApi().push_transaction(key, it)
//                }
//    }
//
//    fun sellram(key1: String, from: String, ram: Long): Flowable<EosSend> {
//        var e: EosInfo? = null
//        return eosApi().get_info(key)
//                .flatMap {
//                    e = it
//                    var j = JsonObject()
//                    j.addProperty("block_num_or_id", it.last_irreversible_block_id)
//                    eosApi().get_block(key, j)
//                }.map {
//                    var content = EosUtils.sellram(key1, from, ram, 60, e!!.chain_id, e!!.head_block_time, e!!.last_irreversible_block_id)
//                    content
//                }.flatMap {
//                    eosApi().push_transaction(key, it)
//                }
//    }
}