package com.conduent.nationalhighways.ui.loader

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.Interceptor


interface RetryListener {
    fun onRetryClick(chain: Interceptor.Chain, context: Context) {
    }

    class RetryListenerImpl : RetryListener {
        override fun onRetryClick(chain: Interceptor.Chain, context: Context) {
            Log.e("TAG", "onRetryClick:--> ")
//            runOnUiThread {
//                showRetryDialog(activity)
//            }
        }

        private fun showRetryDialog(context: Context) {
            Log.e("TAG", "showRetryDialog: ")
            if (context is Activity && !context.isFinishing) {
                Log.e("TAG", "showRetryDialog:--> ")

                val retryDialog = AlertDialog.Builder(context)
                    .setMessage("Network error occurred. Do you want to retry?")
                    .setCancelable(false)
                    .setPositiveButton("Retry") { dialog, _ ->
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
