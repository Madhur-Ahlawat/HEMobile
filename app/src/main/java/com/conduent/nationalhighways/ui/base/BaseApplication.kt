package com.conduent.nationalhighways.ui.base

import android.app.Application
import android.content.Context
import android.util.Log
import com.adobe.marketing.mobile.*
import com.conduent.nationalhighways.BuildConfig.ADOBE_ENVIRONMENT_KEY
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.auth.login.LoginResponse
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService //ms



    private var accountResponse: AccountResponse? = null
    fun getAccountSavedData(): AccountResponse {
        return accountResponse!!
    }

    fun setAccountSavedData(accountResponse: AccountResponse) {
        this.accountResponse = accountResponse
    }

    companion object {
        var flowNameAnalytics:String?=""
        var screenNameAnalytics:String?=""
        var CurrentContext: Context? = null


        var INSTANCE: BaseApplication? = null
        var logoutListener: LogoutListener? = null
        var timer: Timer? = null
        public fun setFlowNameAnalytics1(flowName:String){
            flowNameAnalytics=flowName
        }
        public fun getFlowNameAnalytics1(): String? {
            return flowNameAnalytics
        }

        public fun setScreenNameAnalytics1(screenName:String){
            screenNameAnalytics=screenName
        }
        public fun getScreenNameAnalytics1(): String? {
            return screenNameAnalytics
        }

        fun getNewToken(api: ApiService, sessionManager: SessionManager, delegate: () -> Unit?) {
            sessionManager.fetchRefreshToken()?.let { refresh ->
                var responseOK = false
                var tryCount = 0
                var response: Response<LoginResponse?>? = null

                saveDateinSession(sessionManager)

                    try {
                        response = runBlocking {
                            api.refreshToken(refresh_token = refresh)
                        }
                        responseOK = response?.isSuccessful == true
                    } catch (e: Exception) {
                        responseOK = false
                    }
                if (responseOK) {
                    saveToken(sessionManager, response)
                    if(response?.body()?.mfaEnabled!=null && response.body()?.mfaEnabled?.lowercase() == "true"){
                        sessionManager.saveTwoFAEnabled(true)
                    }
                    else{
                        sessionManager.saveTwoFAEnabled(false)
                    }
                    delegate.invoke()
                }
            }
        }

        fun saveDateinSession(sessionManager: SessionManager) {
            val dateFormat = SimpleDateFormat(Constants.dd_mm_yyyy_hh_mm_ss, Locale.getDefault())
            val dateString = dateFormat.format(Date())
            sessionManager.saveStringData(SessionManager.LAST_TOKEN_TIME, dateString)
        }

        fun getNewToken(api: ApiService, sessionManager: SessionManager) {
            sessionManager.fetchRefreshToken()?.let { refresh ->
                var responseOK = false
                var tryCount = 0
                var response: Response<LoginResponse?>? = null

                while (!responseOK && tryCount < 3) {
                    try {
                        response = runBlocking {
                            api.refreshToken(refresh_token = refresh)
                        }
                        responseOK = response?.isSuccessful == true
                    } catch (e: Exception) {
                        responseOK = false
                    } finally {
                        tryCount++
                    }
                }
                if (responseOK) {
                    saveToken(sessionManager, response)
                }
            }
        }


        fun saveToken(sessionManager: SessionManager, response: Response<LoginResponse?>?) {
            Log.e(
                "TAG",
                "saveToken() called with: sessionManager = $sessionManager, response = $response"
            )
            sessionManager.run {
                saveAuthToken(response?.body()?.accessToken ?: "")
                saveRefreshToken(response?.body()?.refreshToken ?: "")
                saveAuthTokenTimeOut(response?.body()?.expiresIn ?: 0)
            }
        }

        /*
                private fun userSessionStart() {
                    timer?.cancel()
                    timer = Timer()
                    timer?.schedule(object : TimerTask() {
                        override fun run() {
                            Log.d("timeout",Gson().toJson("timeout finally"))
                            logoutListener?.onLogout()
                        }
                    }, Constants.LOCAL_APP_SESSION_TIMEOUT)
                }
        */
    }

    override fun onCreate() {
        INSTANCE = this@BaseApplication
        super.onCreate()
        getFireBaseToken()
        setAdobeAnalytics()
    }

    private fun setAdobeAnalytics() {
        MobileCore.setApplication(this)
        MobileCore.setLogLevel(LoggingMode.DEBUG)

        try {
            Analytics.registerExtension()
            Identity.registerExtension()
            Lifecycle.registerExtension()
            Signal.registerExtension()
            UserProfile.registerExtension()
            //Edge.registerExtension()
            //Assurance.registerExtension()
            MobileCore.start {
                MobileCore.configureWithAppID(ADOBE_ENVIRONMENT_KEY)
                Logg.logging("BaseApplication ", "ADOBE_ENVIRONMENT_KEY $ADOBE_ENVIRONMENT_KEY")
                Logg.logging("BaseApplication ", "it  $it")
            }
        } catch (e: java.lang.Exception) {
            Logg.logging("BaseApplication ", "it InvalidInitException  $e")
        }
        MobileCore.setPrivacyStatus(MobilePrivacyStatus.OPT_IN)
    }

    fun setSessionTime() {
        sessionManager.setSessionTime(Calendar.getInstance().timeInMillis)
    }


    /* private fun navigate() {
         baseContext.startActivity(
             Intent(baseContext, LandingActivity::class.java)
                 .putExtra(Constants.SHOW_SCREEN, Constants.SESSION_TIME_OUT)
                 .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                 .putExtra(Constants.TYPE, Constants.LOGIN)
         )
     }
 */
    private fun getFireBaseToken() {
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            Log.e("PUSHTOKENTAG", "Receiver firebase token is ->: ${task.isSuccessful}")
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
            sessionManager.setFirebasePushToken(task.result)
            Log.e("PUSHTOKENTAG", "Receiver firebase token is ->: ${task.result}")

        }).addOnFailureListener(OnFailureListener {
            Log.e("PUSHTOKENTAG", "Receiver firebase exception is ->: ${it.localizedMessage}")

        })
    }

}