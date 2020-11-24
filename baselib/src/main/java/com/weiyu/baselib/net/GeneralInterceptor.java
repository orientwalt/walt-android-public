package com.weiyu.baselib.net;

import com.weiyu.baselib.net.exception.NullException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * weiweiyu
 * 2020/7/10
 * 575256725@qq.com
 * 13115284785
 */
public class GeneralInterceptor implements Interceptor {

    //...

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder();
        Response response = chain.proceed(requestBuilder.build());

        // Throw specific Exception on HTTP 204 response code
        if (response.code() == 204) {
            throw new NullException("There is no content","204");
        }

        return response; // Carry on with the response
    }
}