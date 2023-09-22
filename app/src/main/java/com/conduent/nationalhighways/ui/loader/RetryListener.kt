package com.conduent.nationalhighways.ui.loader

import android.content.Context
import android.util.Log
import okhttp3.Interceptor

interface RetryListener {
    fun onRetryClick(chain:Interceptor.Chain,context:Context)

}
class RetryListenerImpl:RetryListener{
    override fun onRetryClick(chain: Interceptor.Chain, context: Context) {

    }

}