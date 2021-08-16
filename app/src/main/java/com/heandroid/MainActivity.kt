package com.heandroid

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.heandroid.data.LoginResponse
import com.heandroid.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient

    lateinit var tvForgotPassword: TextView
    lateinit var tfUserName: TextInputEditText
    lateinit var tfPassword: TextInputEditText
    lateinit var btnSignIn: TextView
    lateinit var tvCreateAccount: TextView
    var userName: String = ""
    var password: String = ""
    var LOGIN_TAG: String = "Login Screen::"
    var accessToken:String =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tfUserName = findViewById(R.id.edt_username)
        tfPassword = findViewById(R.id.edt_password)
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
        btnSignIn = findViewById(R.id.btn_signin)
        tvCreateAccount = findViewById(R.id.tv_create_account)

        apiClient = ApiClient()
        sessionManager = SessionManager(this)

        btnSignIn.setOnClickListener {
            loginAuthentication(
                "johnsmith32", "Welcome1!"
                //userName.text.toString(),
                //password.text.toString()
            )
        }


    }

    private fun loginAuthentication(username: String, password: String) {
        Toast.makeText(
            this,
            "user data$username--$password",
            Toast.LENGTH_LONG
        ).show()

        if (isNetworkConnected()) {
            Log.d(LOGIN_TAG, "network connected")


            var clientID = "NY_EZ_Pass_iOS_QA"
            var grantType = "password"
            var agecyId = "12"
            var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
            var value = username
            var password = password
            var validatePasswordCompliance = "true"


            apiClient.getApiService(applicationContext)

                .loginWithField(
                    //clientID, grantType, agecyId, clientSecret, "johnsmith32", "Welcome1!", validatePasswordCompliance
                    clientID,
                    grantType,
                    agecyId,
                    clientSecret,
                    username,
                    password,
                    validatePasswordCompliance

                )

                .enqueue(object : Callback<LoginResponse> {
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        // Error logging in
                        Log.d(LOGIN_TAG, "onFailure::")

                    }

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>,
                    ) {
                        val loginResponse = response.body()
                         Log.d(LOGIN_TAG, "Response ::" + "\n" + loginResponse)

                        if (loginResponse?.statusCode == 0
                            && loginResponse.accessToken != null
                        ) {
                             sessionManager.saveAuthToken(loginResponse.accessToken)
                             sessionManager.saveRefrehToken(loginResponse.refreshToken)
                            accessToken = loginResponse.accessToken
                            val intent = Intent(this@MainActivity, DashboardPage::class.java)
                            var bundle = Bundle()
                            bundle.putString("access_token", accessToken)
                            intent.putExtra("data",bundle)
                            startActivity(intent)
                        } else {
                            // Error logging in
                            Toast.makeText(this@MainActivity, "Please check your login credentials.",Toast.LENGTH_LONG).show()
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

