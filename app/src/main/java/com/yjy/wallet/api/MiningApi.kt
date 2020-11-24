package com.yjy.wallet.api

import com.weiyu.baselib.net.BaseResult
import com.yjy.wallet.bean.HtdfApp
import com.yjy.wallet.bean.htdf.Send
import com.yjy.wallet.bean.htdftx.MyNodeInfo
import com.yjy.wallet.bean.htdftx.Node
import com.yjy.wallet.bean.htdftx.Trade
import com.yjy.wallet.bean.waltbean.AppInfo
import com.yjy.wallet.bean.waltbean.UnWeituoLog
import io.reactivex.Flowable
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 *Created by weiweiyu
 * 575256725@qq.com
 *on 2019/7/5
 *
 * USDP HTDF 查询历史记录api
 */
interface MiningApi {
    @POST("api/purse/newserver")
    fun getserver(@QueryMap map: MutableMap<String, Any>): Flowable<BaseResult<Node>>

    @POST("api/purse/myserver")
    fun myserver(@Query("address") address: String): Flowable<BaseResult<Node>>

    @POST("api/purse/nodedelegation")
    fun nodedelegation(@Query("string") s: String,
                       @Query("address") address: String,
                       @Query("total") total: String,
                       @Query("id") id: String): Flowable<BaseResult<Send>>

    @POST("api/purse/getrelieve")
    fun relievenode(@Query("address") address: String, @Query("id") id: String, @Query("total") total: String, @Query("type") type: String): Flowable<BaseResult<String>>

    @POST("api/purse/profit")
    fun getinfo(@Query("vaddress") vaddress: String, @Query("daddress") daddress: String): Flowable<BaseResult<MyNodeInfo>>

    @POST("api/purse/history_judge")
    fun check(@Query("vaddress") vaddress: String, @Query("daddress") daddress: String): Flowable<BaseResult<Boolean>>

    @POST("api/purse/relievelist")
    fun relievelist(@Query("validator_address") vaddress: String, @Query("delegator_address") daddress: String): Flowable<BaseResult<List<HtdfApp>>>

    @POST("api/purse/history_detail")
    fun history_detail(@Query("validator_address") vaddress: String, @Query("delegator_address") daddress: String,
                       @Query("page") page: String, @Query("limit") limit: String): Flowable<BaseResult<UnWeituoLog>>

    @POST("api/purse/history_off_detail")
    fun history_off_detail(@Query("validator_address") vaddress: String, @Query("delegator_address") daddress: String,
                           @Query("page") page: String, @Query("limit") limit: String): Flowable<BaseResult<List<Trade>>>

    @POST("api/purse/extract")
    fun extract(@QueryMap map: MutableMap<String, Any>): Flowable<BaseResult<String>>

    @POST("api/purse/delegatortotal")
    fun delegatortotal(@QueryMap map: MutableMap<String, Any>): Flowable<BaseResult<String>>

    @POST("api/purse/extract_detail")
    fun extract_detail(@Query("id") id: String): Flowable<BaseResult<AppInfo>>
}
