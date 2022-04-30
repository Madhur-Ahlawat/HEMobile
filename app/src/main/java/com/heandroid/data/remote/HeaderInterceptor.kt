package com.heandroid.data.remote

import android.util.Log
import com.heandroid.utils.common.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeaderInterceptor @Inject constructor(private val sessionManager: SessionManager) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
       // requestBuilder.addHeader("ContentType", "application/x-www-form-urlencoded")

        // If token has been saved, add it to the request
        //var sessionManager = MyApplication.getContext()?.let { SessionManager(it) }

        sessionManager?.let {
            it.fetchAuthToken()?.let {
                Log.d("Interceptor:", it)
                var token = "Bearer $it"
                Log.d("token : ", token)
                requestBuilder.addHeader("Authorization", "Bearer $it")


            }
        }

        return chain.proceed(requestBuilder.build())
    }

}