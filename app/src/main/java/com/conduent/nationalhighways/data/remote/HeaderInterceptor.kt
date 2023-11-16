package com.conduent.nationalhighways.data.remote

import android.content.Context
import android.os.Build
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils.getVersionName
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeaderInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    val context: Context,
) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {

        val requestBuilder = chain.request().newBuilder()
        // requestBuilder.addHeader("ContentType", "application/x-www-form-urlencoded")
        // If token has been saved, add it to the request
        //var sessionManager = MyApplication.getContext()?.let { SessionManager(it) }
        val appVersion = getVersionName()
        val osVersion: String? = Build.VERSION.RELEASE

        sessionManager.let {
            it.fetchAuthToken()?.let { accessToken ->
                if (!chain.request().url.encodedPath.contains("account/vehicle/getPlateInfo")) {
                    requestBuilder.addHeader("Authorization", "Bearer $accessToken")
                }
                requestBuilder.addHeader(
                    "User-Agent",
                    "MobileApp-${appVersion}, Android-${osVersion}"
                )
            }
        }

        return chain.proceed(requestBuilder.build())
    }



}