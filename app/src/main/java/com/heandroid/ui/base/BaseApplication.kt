package com.heandroid.ui.base

import android.app.Application
import android.content.Context
import android.util.Log
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
    }

    fun setSessionTime() {
        sessionManager.setSessionTime(Calendar.getInstance().timeInMillis)
    }


}