package com.heandroid

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.heandroid.data.AccountResponse
import com.heandroid.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardPage : Activity() {
    lateinit var tokenString: String
    private  var ACCOUNT_TAG="Account Screen"
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dashboard)
        apiClient = ApiClient()
        tokenString = findViewById<TextView>(R.id.token_id).toString()
        val token: String = SessionManager.USER_TOKEN
        tokenString = token
        Log.d("DashBoard Page ::token", token)
        Log.d("DashBoard Page ::", tokenString)
        sessionManager=SessionManager(this)
//        sessionManager.fetchAuthToken()?.let {
//            //requestBuilder.addHeader("Authorization", "Bearer $it")
//            Log.d("DashBoard Page ::fetchAuthToken", it)
//            callApiForAccountOverview("Bearer $it")
//        }
        var bundle = intent.getBundleExtra("data")
        bundle?.let {
            var accessToken = it.getString("access_token")
            if (accessToken != null) {
                Log.d("DashBoard Page ::fetchAuthToken", accessToken)
                callApiForAccountOverview("Bearer $accessToken")
            }

        }
        //callApiForAccountOverview()
    }

    private fun callApiForAccountOverview(tokenString:String) {
        if (isNetworkConnected()) {
            Log.d(ACCOUNT_TAG, "network connected")
            apiClient.getApiService(applicationContext)
                .getAccountOverview(tokenString)
                .enqueue(object : Callback<AccountResponse> {
                    override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                        // Error logging in
                        Log.d(ACCOUNT_TAG, "onFailure::")

                    }

                    override fun onResponse(
                        call: Call<AccountResponse>,
                        response: Response<AccountResponse>,
                    ) {
                        val responseBody = response.body()
                        Log.d(ACCOUNT_TAG, "Response ::" + "\n" + responseBody)
//
//                        if (loginResponse?.statusCode == 0
//                            && loginResponse.accessToken != null
//                        ) {
//                            sessionManager.saveAuthToken(loginResponse.accessToken)
//                            sessionManager.saveRefrehToken(loginResponse.refreshToken)
//                        } else {
//                            // Error logging in
//                        }
                    }
                })

        } else {
            AlertDialog.Builder(this).setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setIcon(android.R.drawable.ic_dialog_alert).show()
        }

    }


    /*companion object {

        fun newInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }*/

    /* override fun onCreateView(
         inflater: LayoutInflater,
         container: ViewGroup?,
         savedInstanceState: Bundle?,
     ): View? {
         return inflater.inflate(R.layout.fragment_dashboard, container, false)
     }*/

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }*/


    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}