package com.conduent.nationalhighways.ui.base

import android.app.Application
import com.adobe.marketing.mobile.*
import com.conduent.nationalhighways.BuildConfig.ADOBE_ENVIRONMENT_KEY
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.auth.login.LoginResponse
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.common.SessionManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.util.*
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService //ms
    private var accountResponse: AccountResponse? = null

    fun setAccountSavedData(accountResponse: AccountResponse) {
        this.accountResponse = accountResponse
    }

    companion object {
        var INSTANCE: BaseApplication? = null


        fun getNewToken(api: ApiService, sessionManager: SessionManager, delegate: () -> Unit?) {
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
                    delegate.invoke()
                }
            }
        }




        private fun
                saveToken(sessionManager: SessionManager, response: Response<LoginResponse?>?) {
            sessionManager.run {
                saveAuthToken(response?.body()?.accessToken ?: "")
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

    private fun getFireBaseToken() {
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
            sessionManager.setFirebasePushToken(task.result)

        }).addOnFailureListener {

        }
    }

}