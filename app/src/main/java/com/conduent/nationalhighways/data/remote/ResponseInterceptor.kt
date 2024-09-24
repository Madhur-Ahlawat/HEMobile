package com.conduent.nationalhighways.data.remote

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Window
import android.widget.TextView
import com.conduent.nationalhighways.R
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResponseInterceptor @Inject constructor(private val mContext: Context) :
    Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.e("TAG", "intercept: --Response> ")
        val response = chain.proceed(chain.request())
        return try {
            if (!response.isSuccessful) {
                showDialog(mContext, chain)
                response
            } else {
                response
            }
        } catch (e: Exception) {
            Log.e("TAG", "intercept excepion: " + e)
            response
        }
    }


    private fun showDialog(context: Context, chain: Interceptor.Chain) {
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
}