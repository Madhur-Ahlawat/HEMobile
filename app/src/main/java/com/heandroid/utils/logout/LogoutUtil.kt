package com.heandroid.utils.logout

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.os.AsyncTask
import com.heandroid.ui.base.BaseApplication
import kotlinx.coroutines.*
import okhttp3.internal.wait
import java.util.*


object LogoutUtil {
    var timer: Timer? = null
    private const val LOGOUT_TIME = 60000L
    private var listner: LogoutListener?=null

    @Synchronized
    fun startLogoutTimer(listne: LogoutListener?) {
        if (timer != null) {
            timer?.cancel()
            timer = null
            listner=null
        }
        if (timer == null) {
            timer = Timer()
            listner=listne
            timer?.schedule(object : TimerTask(){
                override fun run() {
                    cancel()
                    timer = null
                    try {
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO){
                                listner?.onLogout()
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            },LOGOUT_TIME)
        }
    }




    @Synchronized
    fun stopLogoutTimer() {
        if (timer != null) {
            listner=null
            timer?.cancel()
            timer = null
        }
    }


}