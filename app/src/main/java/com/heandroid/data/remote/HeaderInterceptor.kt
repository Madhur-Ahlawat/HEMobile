package com.heandroid.data.remote

import android.os.Build
import android.util.Log
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils.getVersionName
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeaderInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        // requestBuilder.addHeader("ContentType", "application/x-www-form-urlencoded")
        // If token has been saved, add it to the request
        //var sessionManager = MyApplication.getContext()?.let { SessionManager(it) }
        val appVersion = getVersionName()
        val osVersion: String? = Build.VERSION.RELEASE

        if (!chain.request().url.encodedPath.contains("oauth/token")){
            sessionManager.let {
                it.fetchAuthToken()?.let { accessToken ->
                    requestBuilder.addHeader("Authorization", "Bearer $accessToken")
                    requestBuilder.addHeader(
                        "User-Agent",
                        "MobileApp-${appVersion}, Android-${osVersion}"
                    )
                }
            }
        }
        return chain.proceed(requestBuilder.build())
    }
}