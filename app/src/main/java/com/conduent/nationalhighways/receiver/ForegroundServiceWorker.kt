package com.conduent.nationalhighways.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.service.PlayLocationService
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils
import java.util.Date

class ForegroundServiceWorker(val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.e("TAG", "doWork: ")
        try {
            if (!Utils.isLocationServiceRunning(context)) {
                Utils.writeInFile(
                    context,
                    "-------- StartLocation Service-------- " + Date() + "--"
                )
                val context = applicationContext
                val intent = Intent(context, PlayLocationService::class.java)
                ContextCompat.startForegroundService(context, intent)
                Utils.writeInFile(
                    context,
                    "-------- SetForeground DOne-------- " + Date() + "--"
                )


//                val request = OneTimeWorkRequestBuilder<HighPriorityWorker>()
//                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//                    .build()
//                WorkManager.getInstance(context).enqueue(request)


                return Result.success()
            }
        } catch (e: Exception) {
            Utils.writeInFile(
                context,
                "-------- SetForeground exception-------- " + e.message + "--"
            )
            return Result.failure()
        }

        return Result.success()

    }
}


class HighPriorityWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        Utils.writeInFile(
            context,
            "getForegroundInfo called " + Date() + "--"
        )
        return ForegroundInfo(
            Constants.FOREGROUND_SERVICE_NOTIFICATIONID, createNotification(context)
        )
    }

    override suspend fun doWork(): Result {

        Utils.writeInFile(
            context,
            "getForegroundInfo dowork called " + Date() + "--"
        )
        return Result.success()
    }

    private fun createNotification(context: Context): Notification {
        val CHANNEL_ID = "my_channel_01"
        val channel = NotificationChannel(
            CHANNEL_ID,
            applicationContext.resources.getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW
        )
        channel.setSound(null, null)
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )
        return Notification.Builder(context, CHANNEL_ID)
            .setContentTitle(applicationContext.resources.getString(R.string.app_name))
            .setSmallIcon(R.drawable.notification_icon)
            .setColor(context.resources.getColor(R.color.red, null))
            .setContentText(applicationContext.resources.getString(R.string.dartcharge_running))
            .build()
    }


}

