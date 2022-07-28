package com.heandroid.data.remote

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Window
import android.widget.TextView
import com.heandroid.R
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

        Log.i("teja1234", "response : " + chain.request().toString())
        val response = chain.proceed(chain.request())

        return try{
            if (!response.isSuccessful) {
                Log.i("teja1234", "response : failed")
//                showDialog(mContext, chain)
                response

            } else {
                Log.i("teja1234", "response : success")
                response
            }
        } catch (e : Exception){
            Log.i("teja1234", "response : exception")
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