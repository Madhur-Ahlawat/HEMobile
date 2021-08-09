package com.heandroid

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.heandroid.data.LoginRequest
import com.heandroid.data.LoginResponse
import com.heandroid.utils.Constants
import com.heandroid.utils.SessionManager
import okhttp3.Interceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MultipartBody
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import okhttp3.FormBody

import okhttp3.RequestBody
import com.android.volley.AuthFailureError

import com.android.volley.VolleyLog

import com.android.volley.VolleyError
import com.android.volley.Request.Method.POST

import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient

    private lateinit var loginButton: Button
    lateinit var userName: EditText
    lateinit var password: EditText
    var LOGIN_TAG: String = "Login Screen::"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginButton = findViewById(R.id.login_Btn)
        userName = findViewById(R.id.userName)
        password = findViewById(R.id.password)

        apiClient = ApiClient()
        sessionManager = SessionManager(this)

        loginButton.setOnClickListener {


           // callLoginApiWithVolley()
            loginAuthentication(
                userName.text.toString(),
                password.text.toString()
            )
        }


    }

    private fun callLoginApiWithVolley()
    {
        var mRequestQueue = Volley.newRequestQueue(this);
        val jsonObjRequest: StringRequest = object : StringRequest(POST,
            Constants.LOGIN_URL,
            com.android.volley.Response.Listener<String?> {
                Log.i("Login response", "Success") },

            com.android.volley.Response.ErrorListener { error ->
                VolleyLog.d("volley", "Error: " + error.message)
                error.printStackTrace()
                Log.e("Login error", "Success")
            }) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["client_id"] = "NY_EZ_Pass_iOS_QA"
                params["grant_type"] = "password"
                params["agecyId"] = "12"
                params["client_secret"] = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
                params["value"] = "Johnsmith32"
                params["password"] = "Welcome1"
                params["validatePasswordCompliance"] = "true"
                return params
            }
        }

        mRequestQueue.add(jsonObjRequest)

    }
    private fun loginAuthentication(username: String, password: String) {
        Toast.makeText(
            this,
            "user data$username--$password",
            Toast.LENGTH_LONG
        ).show()

        if (isNetworkConnected()) {
            Log.d(LOGIN_TAG, "network connected")


           var clientID =  "NY_EZ_Pass_iOS_QA"
           var grantType =  "password"
           var agecyId = "12"
           var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
           var value =  username
           var password =  password
           var validatePasswordCompliance =  "true"


            apiClient.getApiService(applicationContext)

                .loginWithField(
                    //clientID, grantType, agecyId, clientSecret, "johnsmith32", "Welcome1!", validatePasswordCompliance
                    clientID, grantType, agecyId, clientSecret, username, password, validatePasswordCompliance

                )

                .enqueue(object : Callback<LoginResponse> {
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        // Error logging in
                        Log.d(LOGIN_TAG,"onFailure::")

                    }

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val loginResponse = response.body()

                        Log.d(LOGIN_TAG, "Response ::" + "\n" + loginResponse)

                        if (loginResponse?.statusCode == 200
//                            && loginResponse.user != null
                        ) {
                           // sessionManager.saveAuthToken(loginResponse.authToken)
                        } else {
                            // Error logging in
                        }
                    }
                })

        } else {
            AlertDialog.Builder(this).setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setIcon(android.R.drawable.ic_dialog_alert).show()
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


}

