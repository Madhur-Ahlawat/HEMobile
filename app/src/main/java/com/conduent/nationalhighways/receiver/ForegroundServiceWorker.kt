package com.conduent.nationalhighways.receiver

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.service.PlayLocationService
import com.conduent.nationalhighways.utils.common.Utils
import java.util.Date

class ForegroundServiceWorker(val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

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