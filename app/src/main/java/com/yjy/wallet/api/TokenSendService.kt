package com.yjy.wallet.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.weiyu.baselib.net.ApiManager
import com.weiyu.baselib.net.BaseResult
import com.yjy.wallet.bean.TokenSend
import com.yjy.wallet.bean.eth.ERCBalance
import com.yjy.wallet.bean.tokenview.*
import com.yjy.wallet.wallet.WaltType
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

//节点切换不能用单例
class TokenSendService {

    val key = ""
    val baseUrl: String = "http://www.tokenview.com:8088"
    fun sendApi(): TokenSendApi {
        return ApiManager.instance.getService(baseUrl, TokenSendApi::class.java)
    }

    /**
     * 这个接口支持币种：BTC及其分叉币BCH，BCHSV，BTG，BCD，还有高仿币LTC，ZCASH，DOGE，DASH等，另外还有XMR,NEO,ONT
     * 发送ETH/ETC RawTranNetStateChangeReceiver saction到区块链上
     */
    fun send(tx: String, type: WaltType): Flowable<TokenSend> {
        val array = JsonArray()
        array.add(tx)
        val json = when (type) {
            WaltType.ETC, WaltType.ETH -> getMainJsonParam("2.0", "eth_sendRawTransaction")
            else -> getMainJsonParam("1.0", "sendrawtransaction")
        }
        json.add("params", array)
        return sendApi().send(json, getcoin(type), key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    // 广播TRX Transaction到区块链上
    fun send(json: JsonObject, type: WaltType): Flowable<TokenSend> {
        return sendApi().send(json, getcoin(type), key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun utxo(address: String, type: WaltType): Flowable<BaseResult<List<TUtxo>>> {
        return sendApi().getUtxo(getcoin(type), address, key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun tx(tx: String, type: WaltType): Flowable<BaseResult<TTx>> {
        return sendApi().getTx(getcoin(type), tx, key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun utx(tx: String, type: WaltType): Flowable<BaseResult<TUTx>> {
        return sendApi().getUTx(getcoin(type), tx, key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun tokenbalance(address: String, type: WaltType): Flowable<ERCBalance> {
        return sendApi().tokenbalance(getcoin(type), address, key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun balance(address: String, type: WaltType): Flowable<BaseResult<String>> {
        return sendApi().balance(getcoin(type), address, key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getfee(type: WaltType): Flowable<BaseResult<TFee>> {
        return sendApi().getFee(getcoin(type), key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun confirmation(type: WaltType, tx: String): Flowable<BaseResult<TConfirmation>> {
        return sendApi().confirmation(getcoin(type), tx, key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getTokenTxs(address: String, page: String, contractaddress: String, type: WaltType, pageSize: String): Flowable<BaseResult<List<TTokenTx>>> {
        return sendApi().getTokenTx(getcoin(type), contractaddress, address, page, pageSize, key)
    }

    fun getATxs(address: String, page: String, type: WaltType, pageSize: String): Flowable<BaseResult<List<TTx>>> {
        return sendApi().getATxs(getcoin(type), address, page, pageSize, key)
    }

    fun getUTxs(address: String, page: String, type: WaltType, pageSize: String): Flowable<BaseResult<List<TTx>>> {
        return sendApi().getUTxs(getcoin(type), address, page, pageSize.toString(), key)
    }

    fun getToken(contractaddress: String, type: WaltType): Flowable<BaseResult<TToken>> {
        return sendApi().token(getcoin(type), contractaddress, key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getpending(address: String, type: WaltType): Flowable<BaseResult<Any>> {
        return sendApi().pending(getcoin(type), address, key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun inpending(tx: String, type: WaltType): Flowable<TBase> {
        return sendApi().inpending(getcoin(type), tx, key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun nonce(address: String, type: WaltType): Flowable<BaseResult<TAddrInfo>> {
        return sendApi().nonce(getcoin(type), address, key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * BTC, BCH, BCHSV, NEO, DASH, LTC, DOGE, RVN, PIVX, NMC, RDD, XZC, NRG,
     * SYS, NEBL, VTC, VITAE, FTC, GIN, HC, CRW, GAME, PART, EMC, UNO, XSN,
     * WGR, EMC2, BCI, XWC, BLK, CLAM, XVG, LUX, SMART, DCR, BAY, FLO, NAV,
     * STRAT, XMY, BCA, MONA, DGB，LCC 其他币的数据正在导入中，
     * 未来适用于任何Tokenview网站上支持UTXO交易模型的公链，比如BTC及其各种分叉币，
     * 高仿币，暂时不适用于XMR和Zcash等匿名币
     */
    fun getcoin(type: WaltType): String {
        return when (type) {
            WaltType.BSV -> "bchsv"
            else -> {
                type.name.toLowerCase()
            }
        }
    }

    private fun getMainJsonParam(version: String, method: String): JsonObject {
        val json = JsonObject()
        json.addProperty("jsonrpc", version)
        json.addProperty("method", method)
        json.addProperty("id", "waltsend")
        return json
    }

    private fun getTrxJsonParam(from: String, to: String, amount: Long, tx: String): JsonObject {
        val json = JsonObject()
        json.addProperty("from", from)
        json.addProperty("to", to)
        json.addProperty("amount", amount)
        json.addProperty("method", "broadcasttransaction")
        val array = JsonArray()
        array.add(tx)
        json.add("signature", array)
        return json
    }
}