package com.heandroid.utils.logout

import android.os.CountDownTimer
import android.util.Log
import com.heandroid.ui.base.BaseApplication

object LogoutUtil {
    var timer: CountDownTimer? = null
    var LOGOUT_TIME : Long= 1000 * 15
    private var listner: LogoutListener?=null

    @Synchronized
    fun startLogoutTimer(listne: LogoutListener?) {
        if (timer != null) {
            timer?.cancel()
            timer = null
            listner=null
        }
        if (timer == null) {
            listner=listne
            timer =object : CountDownTimer(LOGOUT_TIME,1000){
                override fun onTick(millisUntilFinished: Long) {
                    BaseApplication.INSTANCE.setSessionTime()
                }

                override fun onFinish() {
                    BaseApplication.INSTANCE.setSessionTime()
                    listner?.onLogout()
                }

            }


           timer?.start()

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