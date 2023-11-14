package com.conduent.nationalhighways.ui.loader

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.utils.common.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor


interface RetryCallback {
    fun onRetryClick(
        chain: Interceptor.Chain,
        sessionManager: SessionManager,
        retryListener: RetryCallback,
        dispatchRetry: () -> Unit
    ) {
    }

    class RetryListenerImpl : RetryCallback {
        override fun onRetryClick(
            chain: Interceptor.Chain,
            sessionManager: SessionManager,
            retryListener: RetryCallback,
            dispatchRetry: () -> Unit
        ){
            Log.e("TAG", "onRetryClick:--> ")
            runOnUiThread {
                BaseApplication.CurrentContext?.let {

                    Log.e("TAG", "onRetryClick:dalog " + it)
                    showRetryDialog(it, chain,sessionManager,retryListener,dispatchRetry)
                }
            }
        }


        private fun showRetryDialog(
            context: Context,
            chain: Interceptor.Chain,
            sessionManager: SessionManager,
            retryListener: RetryCallback,
            dispatchRetry: () -> Unit
        ) {
            Log.e("TAG", "showRetryDialog: request -> "+chain.request())
            if (context is Activity && !context.isFinishing) {
                Log.e("TAG", "showRetryDialog:--> ")

                val retryDialog = AlertDialog.Builder(context)
                    .setMessage("Network error occurred. Do you want to retry?")
                    .setCancelable(false)
                    .setPositiveButton("Retry") { dialog, _ ->
                        try {

                            CoroutineScope(Dispatchers.IO).launch {
//                                chain.proceed(chain.request())
                                dispatchRetry.invoke()
                            }

                        } catch (e: Exception) {
                            Log.e("TAG", "showRetryDialog: message " + e.message)
                        }

                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                retryDialog.show()
            }
        }

        private fun runOnUiThread(action: () -> Unit) {
            Handler(Looper.getMainLooper()).post { action.invoke() }
        }
    }
}
