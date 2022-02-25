package com.heandroid.utils.logout

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import com.heandroid.ui.base.BaseApplication
import kotlinx.coroutines.*
import java.util.*


object LogoutUtil {
    var timer: Timer? = null
    private val LOGOUT_TIME = 20000L

    @Synchronized
    fun startLogoutTimer(listner: LogoutListener?) {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
        if (timer == null) {
            timer = Timer()
            timer?.schedule(object : TimerTask(){
                override fun run() {
                    cancel()
                    timer = null
                    try {
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO){
                                var foreGround = async { return@async isAppOnForeground() }.await()
                                if(foreGround){
                                    listner?.onLogout()
                                }
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            },LOGOUT_TIME)
        }
    }


    private fun isAppOnForeground(): Boolean {
        val activityManager = BaseApplication.INSTANCE.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName: String = BaseApplication.INSTANCE.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
                return true
            }
        }
        return false
    }

    @Synchronized
    fun stopLogoutTimer() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }


}