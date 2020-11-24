package com.weiyu.baselib.net

import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.weiyu.baselib.BuildConfig
import com.weiyu.baselib.tinker.util.BaseApplicationContext
import com.weiyu.baselib.util.NetworkUtil
import okhttp3.Cache
import okhttp3.CacheControl
import java.io.File
import java.net.Proxy
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 *Created by weiweiyu
 *on 2019/5/28
 */

class OkHttp private constructor() {
    companion object {
        //设置连接超时的值
        private val CONNECT_TIMEOUT = 40L
        private val READ_TIMEOUT = 60L
        private val WRITE_TIMEOUT = 60L
        val builder: okhttp3.OkHttpClient.Builder by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            val builder = okhttp3.OkHttpClient.Builder()
            //新建一个文件用来缓存网络请求
            //        File cacheDirectory = new File(VinoApplication.getInstance()
            //                .getCacheDir().getAbsolutePath(), "HttpCache");
            //设置连接超时
            builder.retryOnConnectionFailure(true)
            builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) // 连接超时时间阈值
            builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)   // 数据获取时间阈值
            builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)  // 写数据超时时间阈值
            builder.addInterceptor(GeneralInterceptor())
//            val cacheFile = File(BaseApplicationContext.context.cacheDir, "walt")
//            val cache = Cache(cacheFile, 1024 * 1024 * 100) //100Mb
//            builder.cache(cache)
            // Install the all-trusting trust manager
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            builder.sslSocketFactory(sslSocketFactory)
            builder.hostnameVerifier { hostname, session -> true }
            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor("WLog")
                interceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
                interceptor.setColorLevel(Level.INFO)
                builder.addInterceptor(interceptor)
            } else {
                builder.proxy(Proxy.NO_PROXY)//屏蔽抓包
            }
//            builder.addInterceptor { chain ->
//                val request = chain.request()
//                if (!NetworkUtil.isNetworkAvailable(BaseApplicationContext.context)) {
//                    request.newBuilder().cacheControl(CacheControl.FORCE_CACHE)
//                            .build()
//                }
//                var  response=chain.proceed(request)
//                if(NetworkUtil.isNetworkAvailable(BaseApplicationContext.context)){
//                    val cacheControl = request.cacheControl().toString()
//                    response.newBuilder()
//                            .header("Cache-Control", cacheControl)
//                            .removeHeader("Pragma")
//                            .build();
//                }else{
//                    response.newBuilder()
//                            .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
//                            .removeHeader("Pragma")
//                            .build();
//                }
//            }
            builder
        }
    }
}