package com.conduent.nationalhighways.data.remote

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.ui.loader.RetryListener
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
    val retryListener: RetryListener
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
                    "User-Agent", "MobileApp-${appVersion}, Android-${osVersion}"
                )
            }
        }
        var response: Response? = null // Declare the response variable

        try {
            Log.e("TAG", "intercept: ")
            response = chain.proceed(requestBuilder.build())
        } catch (e: Exception) {
            Log.e("TAG", "intercept: exception ")
//            retryListener.onRetryClick(chain, context)
//            showCustomRetryDialog(chain)
        }

        var retryCount = 0
        val maxRetry = 3 // Define your maximum retry count here

        /*
                while (response == null && retryCount < maxRetry) {
                    Log.e("TAG", "checkretryOption: retryCount "+retryCount)
                    try {
                        // Attempt to make the API call
                        response = chain.proceed(chain.request())
                        if (!response.isSuccessful) {
                            // Handle non-successful responses here
                            // You can check response codes, headers, or any other criteria
                            // to decide whether to retry or not
                            if (response.code == 500) {
                                // Server error, retry
                                retryCount++
                                response = null // Reset response to null for retry
                            } else {
                                // Handle other errors or return the response
        //                        return null
                                return response
                            }
                        }
                    } catch (e: IOException) {
                        // Handle network exceptions, e.g., timeouts
                        retryCount++
                        response = null // Reset response to null for retry
                    }
                }
        */
//        checkretryOption(response,chain,)
        return response ?: throw IOException("Response is null")
    }

    private fun checkretryOption(response1: Response?, chain: Interceptor.Chain) {
        var response = response1

    }


    private fun showDialog(context: Context, chain: Interceptor.Chain) {
        Log.e("TAG", "showDialog: url " + chain.request().url)
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_retry)
        val dialogButton = dialog.findViewById<TextView>(R.id.retryBtn)
        dialogButton.setOnClickListener {
            dialog.dismiss()
            val builder = chain.request().newBuilder()
            chain.proceed(builder.build())
        }
        dialog.show()
    }


    private fun showCustomRetryDialog(chain: Interceptor.Chain) {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_retry, null)
        (context as? Activity)?.runOnUiThread {

            AlertDialog.Builder(context).setView(dialogView).setPositiveButton("Retry") { _, _ ->
                // User clicked retry, reset retryCount
                Log.e("TAG", "showCustomRetryDialog: ")
                chain.proceed(chain.request())
            }.setNegativeButton("Cancel") { _, _ ->
                // User clicked cancel, you can handle this as needed
            }.show()
        }
    }


}