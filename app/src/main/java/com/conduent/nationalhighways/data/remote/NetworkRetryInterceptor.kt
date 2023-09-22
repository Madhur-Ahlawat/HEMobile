package com.conduent.nationalhighways.data.remote

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.atomic.AtomicBoolean

class NetworkRetryInterceptor(private val context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        /*
         val request = chain.request()
         var response: Response?=null
        for (retryCount in 0 until maxRetries) {
             Log.e("TAG", "intercept:retryCount "+retryCount )
             Log.e("TAG", "intercept:maxRetries "+maxRetries )

             response = chain.proceed(request)

             if (response.isSuccessful) {
                 return response
             }

             // Check if it's a timeout and if there are retries left
             if (response.code == 504 && retryCount < maxRetries - 1) {
                 // Handle the retry logic here or show a popup
                 // You can implement a callback to show the popup
                 // and retry the API call when the user confirms.
             } else {
                 // If it's not a timeout or there are no retries left, return the response.
                 return response
             }
         }

         return response!! // Return the response even if all retries fail.*/
        val request: Request = chain.request()
        var response = chain.proceed(request)
        var tryCount = 0
        Log.e("TAG", "intercept: tryCount "+tryCount )
        while (!response.isSuccessful && tryCount < 3) {

//            showRetryDialog(response)
            tryCount++
            response.close()
            response = chain.proceed(request)
        }

        return response
    }

//    private fun showRetryDialog(response: Response) {
//        val retryDialog = AlertDialog.Builder(context)
//            .setMessage("Network error occurred. Do you want to retry?")
//            .setCancelable(false)
//            .setPositiveButton("Retry") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .create()
//
//        retryDialog.show()
//    }


}
