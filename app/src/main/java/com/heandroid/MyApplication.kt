package com.heandroid

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application()
{
   companion object {

        private var instance: MyApplication? = null

        fun getInstance(): MyApplication? {
            return instance
        }

        fun getContext(): Context? {
            return instance
        }

    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}