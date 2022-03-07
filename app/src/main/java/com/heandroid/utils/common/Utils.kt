package com.heandroid.utils.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.ui.auth.session.SessionDialog
import com.heandroid.ui.base.BaseApplication
import com.heandroid.ui.landing.LandingActivity
import com.heandroid.utils.extn.changeBackgroundColor
import com.heandroid.utils.extn.changeTextColor
import com.heandroid.utils.logout.LogoutUtil
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object Utils {
    fun hasInternetConnection(application: BaseApplication): Boolean {
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    private val EMAIL = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
        Pattern.CASE_INSENSITIVE
    )
    private const val MIN_PASSWORD_LENGTH = 6


    fun isEmailValid(email: String): Boolean {
        return if (email.isEmpty()) {
            false
        } else {
            EMAIL.matcher(email).matches()
        }
    }

    fun isPasswordValid(password: String?): Boolean {
        return if (password == null || password.trim { it <= ' ' }.isEmpty()) {
            false
        } else {
            password.trim { it <= ' ' }.length >= MIN_PASSWORD_LENGTH
        }
    }

    fun currentDateAndTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.getDefault())
        return sdf.format(Date())

    }

    fun getDirection(entry: String?): String? {
        return when (entry) {
            "N" -> "NORTHBOUND"
            "S" -> "SOUTHBOUND"
            else -> ""
        }
    }

    fun loadStatus(status: String, tvTitle: AppCompatTextView) {
        when (status) {

            "Y" -> {
                tvTitle.text = tvTitle.context.getString(R.string.paid)
                tvTitle.changeTextColor(R.color.color_10403C)
                tvTitle.changeBackgroundColor(R.color.color_CCE2D8)


            }
            "N" -> {
                tvTitle.text = tvTitle.context.getString(R.string.unpaid)
                tvTitle.changeTextColor(R.color.color_10403C)
                tvTitle.changeBackgroundColor(R.color.FCD6C3)

            }

            else -> {
                tvTitle.text = status
                tvTitle.changeTextColor(R.color.color_594D00)
                tvTitle.changeBackgroundColor(R.color.FFF7BF)
            }
        }
    }

    fun sessionExpired(context: AppCompatActivity) {
        LogoutUtil.stopLogoutTimer()
        val dialog=SessionDialog()
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        dialog.show(context.supportFragmentManager,"")
    }

}