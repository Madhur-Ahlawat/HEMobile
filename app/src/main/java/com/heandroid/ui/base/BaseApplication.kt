package com.heandroid.ui.base

import android.app.Application
import com.adobe.marketing.mobile.*
import com.heandroid.BuildConfig.ADOBE_ENVIRONMENT_KEY
import com.heandroid.utils.common.SessionManager
import dagger.hilt.android.HiltAndroidApp
import java.util.*
import javax.inject.Inject


@HiltAndroidApp
class BaseApplication : Application() {

    @Inject
    lateinit var sessionManager: SessionManager

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


}