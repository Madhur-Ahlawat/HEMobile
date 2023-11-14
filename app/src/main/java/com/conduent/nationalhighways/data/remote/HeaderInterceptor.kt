package com.conduent.nationalhighways.data.remote

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.RetryCallback
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils.getVersionName
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeaderInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    val context: Context,
    val retryListener: RetryCallback
) : Interceptor {

    var dispatchRetry: (() -> Unit)? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        Log.e("TAG", "showRetryDialog: request ---> "+chain.request())

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

        var response: Response? = null // Declare the response variable

        try {
            Log.e("TAG", "intercept: ")
            response = chain.proceed(requestBuilder.build())

        } catch (e: Exception) {
            var retrycount = sessionManager.fetchIntData(SessionManager.RETRY_API_TIME)
            Log.e("TAG", "intercept: exception " + retrycount)

            sessionManager.saveIntData(SessionManager.RETRY_API_TIME, retrycount++)
            Log.e("TAG", "intercept: exception !! " + retrycount)

            if (retrycount <= 3) {
                response=null
                dispatchRetry ={ response=chain.proceed(chain.request())}
                retryListener.onRetryClick(chain, sessionManager, retryListener, dispatchRetry!!)
//                response?.close()
            } else {
                sessionManager.saveIntData(SessionManager.RETRY_API_TIME, 0)
                val intent = Intent(context, LandingActivity::class.java)
                intent.putExtra(Constants.SHOW_SCREEN, Constants.FAILED_RETRY_SCREEN)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }

        }

        return response ?: throw IOException("Response is null")

//        return chain.proceed(requestBuilder.build())
    }



}