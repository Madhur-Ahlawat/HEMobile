package com.heandroid.ui.base

import android.app.Application
import android.os.CountDownTimer
import com.heandroid.data.model.auth.login.LoginResponse
import com.heandroid.data.remote.ApiService
import com.adobe.marketing.mobile.*
import com.heandroid.BuildConfig.ADOBE_ENVIRONMENT_KEY
import com.heandroid.utils.common.SessionManager
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
    lateinit var api: ApiService

    private var timer: CountDownTimer? = null
    private val TICK_TIME: Long = 1000 //ms
    private val TIME_PERIOD: Long = 3000 //ms

    companion object {
        lateinit var INSTANCE: BaseApplication
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
            MobileCore.start {

                MobileCore.configureWithAppID(ADOBE_ENVIRONMENT_KEY)
            }
        } catch (e: InvalidInitException) {

        }
    }

    fun setSessionTime() {
        sessionManager.setSessionTime(Calendar.getInstance().timeInMillis)
    }

    fun initTimerObject(timePeriod: Long = TIME_PERIOD) {
        timer = object :
            CountDownTimer(
                timePeriod,
                TICK_TIME
            ) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
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
                    }
                }
            }
        }
    }

    private fun saveToken(response: Response<LoginResponse?>?) {
        sessionManager.saveAuthToken(response?.body()?.accessToken ?: "")
        val time = (sessionManager.fetchAuthTokenTimeout() - 30) * 1000 //ms
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