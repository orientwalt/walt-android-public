package com.yjy.wallet.api

import com.google.gson.JsonObject
import com.weiyu.baselib.net.BaseResult
import com.yjy.wallet.bean.TokenSend
import com.yjy.wallet.bean.eth.ERCBalance
import com.yjy.wallet.bean.tokenview.*
import io.reactivex.Flowable
import retrofit2.http.*

/**
 * weiweiyu
 * 2019/12/18
 * 575256725@qq.com
 * 13115284785
 */
interface TokenSendApi {
    @POST("onchainwallet/{coin}/")
    fun send(@Body json: JsonObject, @Path("coin") coin: String, @Query("apikey") apikey: String): Flowable<TokenSend>

    @GET("unspent/{coin}/{address}/1/200")
    fun getUtxo(@Path("coin") coin: String, @Path("address") address: String, @Query("apikey") apikey: String): Flowable<BaseResult<List<TUtxo>>>

    @GET("tx/{coin}/{tx}")
    fun getTx(@Path("coin") coin: String, @Path("tx") tx: String, @Query("apikey") apikey: String): Flowable<BaseResult<TTx>>

    @GET("tx/{coin}/{tx}")
    fun getUTx(@Path("coin") coin: String, @Path("tx") tx: String, @Query("apikey") apikey: String): Flowable<BaseResult<TUTx>>

    @GET("addr/b/{coin}/{address}")
    fun balance(@Path("coin") coin: String, @Path("address") address: String, @Query("apikey") apikey: String): Flowable<BaseResult<String>>

    /**
     * 通过公链简称和代币地址，获得该地址交易某种代币的历史列表
     * TOKENVIEW.COM上，支持智能合约的公链目前有ETH，ETC，NAS，NEO，TRX，IOST，ONT
     */
//    @Headers("Cache-Control: public, max-age=60")
    @GET("{coin}/address/tokentrans/{address}/{caddress}/{page}/{pageSize}")
    fun getTokenTx(@Path("coin") coin: String, @Path("caddress") caddress: String,
                   @Path("address") address: String, @Path("page") page: String,
                   @Path("pageSize") pageSize: String, @Query("apikey") apikey: String): Flowable<BaseResult<List<TTokenTx>>>

    //这个接口适用于所有支持智能合约的公链，比如ETH/ETC，TRX，ONT，IOST等
    @GET("{coin}/address/normal/{address}/{page}/{pageSize}")
    fun getATxs(@Path("coin") coin: String,
                @Path("address") address: String, @Path("page") page: String,
                @Path("pageSize") pageSize: String, @Query("apikey") apikey: String): Flowable<BaseResult<List<TTx>>>

    //该接口适用于非智能合约的公链，比如BTC及其各种分叉币，高仿币
    @GET("address/{coin}/{address}/{page}/{pageSize}")
    fun getUTxs(@Path("coin") coin: String,
                @Path("address") address: String, @Path("page") page: String,
                @Path("pageSize") pageSize: String, @Query("apikey") apikey: String): Flowable<BaseResult<List<TTx>>>

    /**
     * 通过公链简称和持币地址，获得该持币地址持有的所有代币的余额，以及代币的基本信息
     * TOKENVIEW.COM上，支持智能合约的公链目前有ETH，ETC，NAS，NEO，TRX，IOST，ONT
     */
    @GET("{coin}/address/tokenbalance/{address}")
    fun tokenbalance(@Path("coin") coin: String, @Path("address") address: String,
                     @Query("apikey") apikey: String): Flowable<ERCBalance>

    @GET("pendingstat/{coin}")
    fun getFee(@Path("coin") coin: String, @Query("apikey") apikey: String): Flowable<BaseResult<TFee>>

    @GET("tx/confirmation/{coin}/{tx}")
    fun confirmation(@Path("coin") coin: String, @Path("tx") tx: String, @Query("apikey") apikey: String): Flowable<BaseResult<TConfirmation>>

    @GET("{coin}/token/{address}")
    fun token(@Path("coin") coin: String, @Path("address") address: String, @Query("apikey") apikey: String): Flowable<BaseResult<TToken>>

    @GET("pending/ntx/{coin}/{address}")
    fun pending(@Path("coin") coin: String, @Path("address") address: String, @Query("apikey") apikey: String): Flowable<BaseResult<Any>>

    @GET("pending/{coin}/{tx}")
    fun inpending(@Path("coin") coin: String, @Path("tx") tx: String, @Query("apikey") apikey: String): Flowable<TBase>

    @GET("{coin}/address/{address}")
    fun nonce(@Path("coin") coin: String, @Path("address") address: String, @Query("apikey") apikey: String): Flowable<BaseResult<TAddrInfo>>
}