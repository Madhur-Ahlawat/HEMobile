package com.heandroid.utils

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import com.heandroid.ui.base.BaseApplication
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner: AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }

    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
    }
}
//
//@CustomTestApplication(MyTestApplication::class)
//interface HiltTestApplication
//
//open class MyTestApplication : Application() {
//    companion object {
//        lateinit var INSTANCE: MyTestApplication
//    }
//
//    override fun onCreate() {
//        INSTANCE = this
//        super.onCreate()
//    }
//}