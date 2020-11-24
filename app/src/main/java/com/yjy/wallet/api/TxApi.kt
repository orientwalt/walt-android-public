package com.yjy.wallet.api

import com.weiyu.baselib.net.BaseResult
import com.yjy.wallet.bean.htdf.AccountInfo
import com.yjy.wallet.bean.htdf.HRCBean
import com.yjy.wallet.bean.htdftx.HtdfBalance
import com.yjy.wallet.bean.htdftx.Trade
import com.yjy.wallet.bean.htdftx.TradeRep
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *Created by weiweiyu
 * 575256725@qq.com
 *on 2019/7/5
 *
 * USDP HTDF 查询历史记录api
 */
interface TxApi {
    @GET("api/getRecord")
    fun getTx(@Query("address") address: String, @Query("page") page: String, @Query("limit") limit: String, @Query("height") height: Int): Flowable<TradeRep>

    @GET("api/getContract")
    fun getTokenTx(@Query("contract") contract: String, @Query("address") address: String, @Query("page") page: String, @Query("limit") limit: String, @Query("height") height: Int): Flowable<TradeRep>

    @GET("api/getHistory")
    fun getHistory(@Query("address") address: String, @Query("height") height: String, @Query("type") type: String): Flowable<TradeRep>

    @GET("api/blocklog")
    fun blocklog(@Query("address") address: String, @Query("to") to: String, @Query("type") type: String, @Query("page") page: String, @Query("limit") limit: String): Flowable<BaseResult<List<Trade>>>

    @GET("api/accountInfo")
    fun accountInfo(@Query("userhash") address: String, @Query("type") type: String): Flowable<HtdfBalance>

    //HTDF
    @GET("api/third/getaddressinfo")
    fun getinfo(@Query("address") address: String): Flowable<BaseResult<AccountInfo>>

    @GET("api/third/getblock")
    fun getBlock(): Flowable<String>

    @GET("api/token_balance")
    fun balance(@Query("address") address: String): Flowable<BaseResult<List<HRCBean>>>

    @GET("api/third/gettokeninfo")
    fun gettokeninfo(@Query("contract_address") contract_address: String): Flowable<BaseResult<List<HRCBean>>>

}
