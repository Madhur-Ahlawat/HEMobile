package com.heandroid.network

import com.heandroid.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HttpLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()

        // Request customization: add request headers

        // Request customization: add request headers
       // var token = SessionManager(this).
        val requestBuilder: Request.Builder = original.newBuilder()
            .header("Authorization", "auth-value") // <-- this is the important line


        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }
}