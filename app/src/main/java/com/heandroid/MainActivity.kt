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
import com.heandroid.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            loginAuthentication(
                userName.text.toString(),
                password.text.toString()
            )

        }


    }

    private fun loginAuthentication(username: String, password: String) {
        Toast.makeText(
            this,
            "user data$username--$password",
            Toast.LENGTH_LONG
        ).show()

        //if (isNetworkConnected()) {
            Log.d(LOGIN_TAG, "network connected")

            apiClient.getApiService(applicationContext)
                .login(
                    LoginRequest(
                        clientId = "NY_EZ_Pass_iOS_QA",
                        grantType = "password",
                        agecyId = 12,
                        clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7",
                        value = username,
                        password = password,
                        validatePasswordCompliance = "true"
                    )
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

                        /*if (loginResponse?.statusCode == 200 && loginResponse.user != null) {
                            sessionManager.saveAuthToken(loginResponse.authToken)
                        } else {
                            // Error logging in
                        }*/
                    }
                })


        /*} else {
            AlertDialog.Builder(this).setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setIcon(android.R.drawable.ic_dialog_alert).show()
        }*/
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

