package com.heandroid.ui.base

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BaseApplication : Application() {
    companion object {
        lateinit var INSTANCE: BaseApplication
    }

    override fun onCreate() {
        INSTANCE = this@BaseApplication
        super.onCreate()
    }


}