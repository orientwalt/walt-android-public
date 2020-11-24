package com.yjy.wallet.api.unuse

import com.yjy.wallet.bean.eth.*
import io.reactivex.Flowable
import org.web3j.protocol.core.methods.response.EthCall
import org.web3j.protocol.core.methods.response.EthGetCode
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.core.methods.response.EthTransaction
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface ETHApi {

    @GET("/api")
    fun getTxs(@QueryMap map: MutableMap<String, String>): Flowable<ETHTx>

    @GET("/api")
    fun balance(@QueryMap map: MutableMap<String, String>): Flowable<ETHBalance>

    @GET("/api")
    fun check(@QueryMap map: MutableMap<String, String>): Flowable<ETHTxCheck>

    @GET("/api")
    fun checktoken(@QueryMap map: MutableMap<String, String>): Flowable<ETHTokenCheck>

    @GET("/api")
    fun tokenBalance(@QueryMap map: MutableMap<String, String>): Flowable<ETHBalance>

    @GET("/api")
    fun tokenTx(@QueryMap map: MutableMap<String, String>): Flowable<TokenTx>

    @GET("/api")
    fun tokensupply(@QueryMap map: MutableMap<String, String>): Flowable<ETHBalance>

    @POST("/api")
    fun send(@QueryMap map: MutableMap<String, String>): Flowable<EthSendTransaction>

    @GET("/api")
    fun gasoracle(@QueryMap map: MutableMap<String, String>): Flowable<ETHGas>

    @GET("/api")
    fun gettx(@QueryMap map: MutableMap<String, String>): Flowable<EthTransaction>

    @GET("/api")
    fun getCode(@QueryMap map: MutableMap<String, String>): Flowable<EthGetCode>

    @GET("/api")
    fun eth_call(@QueryMap map: MutableMap<String, String>): Flowable<EthCall>
}
