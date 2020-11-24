package com.cjwsc.idcm.net.factory

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.weiyu.baselib.net.BaseResult
import com.weiyu.baselib.net.exception.NullException
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import kotlin.text.Charsets.UTF_8

class CustomGsonResponseBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val response = value.string()
        val httpStatus = gson.fromJson(response, BaseResult::class.java)
//        if (httpStatus.errCode != 0 && httpStatus.errCode != 10162) {
//            value.close()
//            //不为-1，表示响应数据不正确，抛出异常
//            throw NullException(httpStatus.errMsg, httpStatus.errMsg.toString())
//        }

        //继续处理body数据反序列化，注意value.string() 不可重复使用
        val contentType = value.contentType()
        val charset = if (contentType != null) contentType.charset(UTF_8) else UTF_8
        val inputStream = ByteArrayInputStream(response.toByteArray())
        val reader = InputStreamReader(inputStream, charset!!)
        val jsonReader = gson.newJsonReader(reader)

        value.use {
            return adapter.read(jsonReader)
        }
    }
}
