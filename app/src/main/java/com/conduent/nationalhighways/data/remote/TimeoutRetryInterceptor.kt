package com.conduent.nationalhighways.data.remote

import android.content.Context
import android.content.Intent
import com.conduent.nationalhighways.BuildConfig
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.RetryCallback
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

class TimeoutRetryInterceptor(
    private val context: Context,
    private val retryListener: RetryCallback
) : Interceptor {

    var dispatchTimeout: (() -> Unit)? = null
    override fun intercept(chain: Interceptor.Chain): Response {

        var response: Response? = null // Declare the response variable

        try {
            response = chain.proceed(chain.request())

        } catch (e: Exception) {
            BaseApplication.CurrentContext?.let { it1 -> Utils.showProgressBar(it1, false) }
            dispatchTimeout = {
                retryCall(chain.request(), retryListener, 0)
            }
            retryListener.onTimeOut(chain.request(), dispatchTimeout!!, 0)
        }

        return response ?: throw IOException("Response is null")
    }

    fun retryCall(request: Request, retryListener: RetryCallback, retryCount: Int) {

        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) builder.addInterceptor(logging)
            .callTimeout(0, TimeUnit.SECONDS)
            .connectTimeout(
                Constants.TIME_OUT_SEC, TimeUnit.SECONDS
            )
            .readTimeout(
                Constants.TIME_OUT_SEC, TimeUnit.SECONDS
            )
            .writeTimeout(
                Constants.TIME_OUT_SEC, TimeUnit.SECONDS
            )
        val okHttpClient = builder.build()

        okHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (retryCount < 2) {
                    val count = retryCount + 1
                    dispatchTimeout = {
                        retryCall(request, retryListener, count)
                    }
                    retryListener.onTimeOut(request, dispatchTimeout!!, count)
                } else {
                    val intent = Intent(context, LandingActivity::class.java)
                    intent.putExtra(Constants.SHOW_SCREEN, Constants.FAILED_RETRY_SCREEN)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }

            }

            override fun onResponse(call: Call, response: Response) {

            }

        })

    }


}
