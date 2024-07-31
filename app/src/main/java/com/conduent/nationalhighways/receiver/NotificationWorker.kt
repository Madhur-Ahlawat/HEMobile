package com.conduent.nationalhighways.receiver

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.notification.NotificationUtils

class NotificationWorker (val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    lateinit var notificationUtils: NotificationUtils

    override fun doWork(): Result {
        Log.e("TAG", "doWork: " )
        notificationUtils = NotificationUtils(context)
        showNotificationForGeofence()
        return Result.success()
    }

    private fun showNotificationForGeofence() {
        notificationUtils.showNotification(
            context.resources.getString(R.string.str_did_you_cross_today),
            context.resources.getString(R.string.str_responsible_paying),
            Constants.GEO_FENCE_NOTIFICATION
        )
    }
}