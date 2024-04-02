package com.conduent.nationalhighways.ui.base

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.adobe.marketing.mobile.*
import com.conduent.nationalhighways.BuildConfig.ADOBE_ENVIRONMENT_KEY
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.data.model.auth.login.LoginResponse
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.receiver.BootReceiver
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


    private var ProfileDetailModel: ProfileDetailModel? = null
    fun getAccountSavedData(): ProfileDetailModel {
        return ProfileDetailModel!!
    }

    fun setAccountSavedData(ProfileDetailModel: ProfileDetailModel) {
        this.ProfileDetailModel = ProfileDetailModel
    }

    companion object {
        var flowNameAnalytics: String? = ""
        var screenNameAnalytics: String? = ""
        var CurrentContext: Context? = null


        var INSTANCE: BaseApplication? = null
        var logoutListener: LogoutListener? = null
        var timer: Timer? = null
        fun setFlowNameAnalytics1(flowName: String) {
            flowNameAnalytics = flowName
        }

        fun getFlowNameAnalytics1(): String? {
            return flowNameAnalytics
        }

        fun setScreenNameAnalytics1(screenName: String) {
            screenNameAnalytics = screenName
        }

        fun getScreenNameAnalytics1(): String? {
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
                    if (response?.body()?.mfaEnabled != null && response.body()?.mfaEnabled?.lowercase() == "true") {
                        sessionManager.saveTwoFAEnabled(true)
                    } else {
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
            sessionManager.run {
                saveAuthToken(response?.body()?.accessToken ?: "")
                saveBooleanData(SessionManager.SendAuthTokenStatus, true)
                saveRefreshToken(response?.body()?.refreshToken ?: "")
                saveAuthTokenTimeOut(response?.body()?.expiresIn ?: 0)
            }
        }

    }

    override fun onCreate() {
        INSTANCE = this@BaseApplication
        super.onCreate()
        getFireBaseToken()
        setAdobeAnalytics()

        registerBootReceiver()
    }

    private fun registerBootReceiver() {
        val receiver = BootReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BOOT_COMPLETED)
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addDataScheme("com.conduent.nationalhighways")
        }
        INSTANCE?.registerReceiver(receiver, filter)
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