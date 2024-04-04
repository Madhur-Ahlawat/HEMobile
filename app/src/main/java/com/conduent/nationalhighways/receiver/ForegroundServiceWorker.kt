package com.conduent.nationalhighways.receiver

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.common.Utils

class ForegroundServiceWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        Log.e("TAG", "doWork: " )
        // Start the foreground service from the Worker
//        if(!isNotificationActive(context,1122)){
//            val notification = createNotification()
//            showNotification(notification)
//            Log.e("TAG", "doWork:--> " )
//        }
//        // Start the foreground service from the Worker
        if(!Utils.isLocationServiceRunning(context)){
            startForegroundService()
        }
        Log.e("TAG", "doWork:--@@> " )
        return Result.success()
    }

    fun isNotificationActive(context: Context, notificationId: Int): Boolean {
        val notificationManager = NotificationManagerCompat.from(context)
        val activeNotifications = notificationManager.activeNotifications
        for (notification in activeNotifications) {
            if (notification.id == notificationId) {
                // Notification with the specified ID is active
                return true
            }
        }
        // Notification with the specified ID is not active
        return false
    }

    private fun showNotification(notification: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(applicationContext)) {
            createNotificationChannel()
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(1122, notification.build())
        }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "my_channel_01",
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "This channel is used for the foreground service"
            }

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): NotificationCompat.Builder {
        val notificationBuilder = NotificationCompat.Builder(applicationContext, "my_channel_01")
            .setContentTitle(applicationContext.resources.getString(R.string.app_name))
            .setSmallIcon(R.drawable.notification_icon)
            .setColor(context.resources.getColor(R.color.red, null))
            .setContentText(applicationContext.resources.getString(R.string.dartcharge_running))
        // Add any additional actions or customization as needed

        return notificationBuilder
    }


    private fun startForegroundService() {
        val serviceIntent = applicationContext.packageManager?.getLaunchIntentForPackage(applicationContext.packageName)
        serviceIntent?.let {
            it.action = "startForegroundService"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(it)
            } else {
                applicationContext.startService(it)
            }
        }
    }
}