package com.heandroid.network

import android.util.Log
import com.heandroid.MyApplication
import com.heandroid.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
       // requestBuilder.addHeader("ContentType", "application/x-www-form-urlencoded")

        // If token has been saved, add it to the request
        var sessionManager = MyApplication.getContext()?.let { SessionManager(it) }
        sessionManager!!.fetchAuthToken()?.let {
            Log.d("Interceptor:" , it)

            //requestBuilder.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJBZ2VuY3lJZCI6MTgsIkJyb2tlcklkIjozMTk3LCJ1c2VyX25hbWUiOjMxOTcsIkZpcnN0TmFtZSI6bnVsbCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9DTElFTlQiXSwiY2xpZW50X2lkIjoiSEVfTUFQUF9OUCIsImlzUGFzc3dvcmRDb21wbGlhbnQiOmZhbHNlLCJJbnRlcm5hbEFnZW5jeUlkIjowLCJzY29wZSI6WyJ2ZWN0b3JBUEkiLCJmZWF0dXJlSGl0Il0sIlBlcm1pc3Npb24iOm51bGwsIkxhc3ROYW1lIjpudWxsLCJpc1Bhc3N3b3JkRXhwaXJlZCI6ZmFsc2UsImV4cCI6MTY0MzY1MDczMiwicmVxdWlyZTJGQSI6ZmFsc2UsImp0aSI6IjRhMDkxZTMyLTBkN2YtNGIyMS1iNWJkLWIzNzk3YzFhZjJhYiJ9.Rhe3E9UG-BfoZecFpCSI1nCR0tmhdRrIciq4m49KBnNXIbbaR-H-w4fSQOmaA27nyA1uMhtckTPlP85d6AKTcpUGQhkoLCoexCyPp__sJfh6WJdCzhAXVxBVgduZOn5ixM0-FmufsvkY3pKG7rehKGCtZ86-44YWR7n_QTXDSk7qkv0zjzAI6ROXhzQZUUqNxWcpGLyKtx7TBG002G11EYtROgKXh4kuDw2EZ4XPto2VmFJ0Bz5DilIchVgdGpScMsNyBDiqAwfRaJtxaxbi8rI5RH-kwGjtyjl3nSjRMrGXKlo7wuZaV_ubyGhcpjf2GomPoB8H2jZznzCOnp6oXw")
            var token = "Bearer $it"
            Log.d("token : " , token)
            requestBuilder.addHeader("Authorization", "Bearer $it")

        }

        return chain.proceed(requestBuilder.build())
    }

}