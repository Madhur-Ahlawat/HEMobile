package com.conduent.nationalhighways.ui.loader

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.conduent.nationalhighways.databinding.DialogRetryBinding
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.utils.common.Utils
import okhttp3.Request


interface RetryCallback {
    fun onTimeOut(
        request: Request,
        dispatchRetry: () -> Unit,
        retryCount: Int
    ) {
    }

    class RetryListenerImpl : RetryCallback {

        override fun onTimeOut(
            request: Request,
            dispatchRetry: () -> Unit,
            retryCount: Int
        ) {
            Log.e("TAG", "onRetryClick:--> ")
            runOnUiThread {
                BaseApplication.CurrentContext?.let {
                    showRetryDialog(it, dispatchRetry)
                }
            }
        }

        private fun showRetryDialog(
            context: Context,
            dispatchRetry: () -> Unit
        ) {
            if (context is Activity && !context.isFinishing) {
                val dialog = Dialog(context)
                dialog.setCancelable(false)
                val binding: DialogRetryBinding =
                    DialogRetryBinding.inflate(LayoutInflater.from(context))
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setCanceledOnTouchOutside(false)
                dialog.setContentView(binding.root)

                dialog.window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
                ) //Controlling width and height.


                binding.retryBtn.setOnClickListener {
                    BaseApplication.CurrentContext?.let { it1 -> Utils.showProgressBar(it1, true) }
//                    BaseApplication.CurrentContext.showToast()
                    dispatchRetry.invoke()
                    dialog.cancel()
                }
                dialog.show()
            }
        }

        private fun runOnUiThread(action: () -> Unit) {
            Handler(Looper.getMainLooper()).post { action.invoke() }
        }
    }


}
