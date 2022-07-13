package com.heandroid.utils.logout

import android.os.CountDownTimer
import com.heandroid.ui.base.BaseApplication

object LogoutUtil {
    private var timer: CountDownTimer? = null
    var LOGOUT_TIME : Long= 1000 * 300
    private var listner: LogoutListener?=null

    private var isTimeFinish=false

    @Synchronized
    fun startLogoutTimer(listne: LogoutListener?) {
        if (timer != null) {
            timer?.cancel()
            timer = null
            listner=null
        }

        isTimeFinish=false
        if (timer == null) {
            listner=listne
            timer =object : CountDownTimer(LOGOUT_TIME,1000){
                override fun onTick(millisUntilFinished: Long) {
                    BaseApplication.INSTANCE.setSessionTime()
                }

                override fun onFinish() {
                    if(!isTimeFinish){
                        BaseApplication.INSTANCE.setSessionTime()
                        listner?.onLogout()
                    }
                }

            }


           timer?.start()

        }
    }




    @Synchronized
    fun stopLogoutTimer() {
        if (timer != null) {
            listner=null
            isTimeFinish=true
            timer?.cancel()
            timer = null
        }
    }


}