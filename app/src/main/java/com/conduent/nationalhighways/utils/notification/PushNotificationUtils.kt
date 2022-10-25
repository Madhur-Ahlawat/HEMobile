package com.conduent.nationalhighways.utils.notification

import android.content.Context
import android.os.Build

object PushNotificationUtils {

    fun getOSName(): String {
        return Build.MODEL
    }

    fun getOSVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun getAppVersion(context: Context): String {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        return info.versionName
        //return "${info.versionName} (${PackageInfoCompat.getLongVersionCode(info)})"
    }

}