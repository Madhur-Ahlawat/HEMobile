package com.heandroid.ui.base

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import com.heandroid.data.model.auth.login.LoginResponse
import com.heandroid.data.remote.ApiService
import com.adobe.marketing.mobile.*
import com.heandroid.BuildConfig.ADOBE_ENVIRONMENT_KEY
import com.heandroid.utils.common.Logg
import com.heandroid.utils.common.SessionManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.util.*
import javax.inject.Inject


@HiltAndroidApp
class BaseApplication : Application() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService

    private var timer: CountDownTimer? = null
    private val TICK_TIME: Long = 1000 //ms
    private val TIME_PERIOD: Long = 3000 //ms

    companion object {
        var INSTANCE: BaseApplication? = null
    }

    override fun onCreate() {
        INSTANCE = this@BaseApplication
        super.onCreate()
        MobileCore.setApplication(this)
        MobileCore.setLogLevel(LoggingMode.DEBUG)

        try {
            Analytics.registerExtension()
            Identity.registerExtension()
            Lifecycle.registerExtension()
            Signal.registerExtension()
            UserProfile.registerExtension()
//            Edge.registerExtension()
//            Assurance.registerExtension()
            MobileCore.start {
                MobileCore.configureWithAppID(ADOBE_ENVIRONMENT_KEY)


                Logg.logging("BaseApplication ","ADOBE_ENVIRONMENT_KEY $ADOBE_ENVIRONMENT_KEY")
                Logg.logging("BaseApplication ","it  ${it.toString()}")
            }
        } catch (e: java.lang.Exception) {
            Logg.logging("BaseApplication ","it InvalidInitException  ${e.toString()}")

        }
        MobileCore.setPrivacyStatus(MobilePrivacyStatus.OPT_IN)

    }

    fun setSessionTime() {
        sessionManager.setSessionTime(Calendar.getInstance().timeInMillis)
    }

    fun initTimerObject(timePeriod: Long = TIME_PERIOD) {
        Log.i(
            "teja12345",
            "refresh will call in ${timePeriod / 1000} sec or ${(timePeriod / 1000) / 60} min"
        )
        timer = object :
            CountDownTimer(
                timePeriod,
                TICK_TIME
            ) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                Log.i("teja12345", "refresh : called")
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
//                            if (tryCount == 2 && !responseOK) {
//                                navigate()
//                            }
                            tryCount++
                        }
                    }
                    if (responseOK) {
                        saveToken(response)
                    } else
                        Log.i("teja12345", "refresh : failed")
                }
            }
        }
    }

    private fun saveToken(response: Response<LoginResponse?>?) {
        Log.i("teja12345", "refresh : success")
        sessionManager.saveAuthToken(response?.body()?.accessToken ?: "")
        sessionManager.saveRefreshToken(response?.body()?.refreshToken ?: "")
        sessionManager.saveAuthTokenTimeOut(response?.body()?.expiresIn ?: 0)
        val time = kotlin.math.abs((sessionManager.fetchAuthTokenTimeout() - 100) * 1000) //ms
        initTimerObject(time.toLong())
        startTimerAPi()
    }

    fun startTimerAPi() {
        if (timer != null)
            timer?.start()
    }

    fun stopTimerAPi() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

//    private fun navigate() {
//        baseContext.startActivity(
//            Intent(baseContext, LandingActivity::class.java)
//                .putExtra(Constants.SHOW_SCREEN, Constants.SESSION_TIME_OUT)
//                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                .putExtra(Constants.TYPE, Constants.LOGIN)
//        )
//    }

}