package com.conduent.nationalhighways.data.remote

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.conduent.nationalhighways.BuildConfig
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.RetryCallback
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.getVersionName
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeaderInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    val context: Context,
    val retryListener: RetryCallback,
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