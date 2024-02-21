package com.conduent.nationalhighways.utils.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.conduent.nationalhighways.BuildConfig
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.common.Constants

class NotificationUtils(val context: Context) {

    private var channelId = BuildConfig.APPLICATION_ID
    lateinit var notificationManager: NotificationManager
    lateinit var intent: Intent
    lateinit var builder: NotificationCompat.Builder
    lateinit var notificationChannel: NotificationChannel

    fun showNotification(title: String, message: String, type: String) {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (type == Constants.GEO_FENCE_NOTIFICATION) {
            intent = Intent(context, LandingActivity::class.java)
            intent.putExtra(Constants.SHOW_SCREEN, Constants.LANDING_SCREEN)
            intent.putExtra(Constants.NAV_FLOW_FROM, Constants.CHECK_FOR_PAID_CROSSINGS_ONEOFF)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, message, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
            builder = NotificationCompat.Builder(context, channelId).apply {
                setContentTitle(title)
                setContentText(message)
                setSmallIcon(R.drawable.notification_icon)
                setContentIntent(pendingIntent)
                setStyle(NotificationCompat.BigTextStyle().bigText(message))
                setColor(context.resources.getColor(R.color.red, null))
                setAutoCancel(true)
            }
        } else {
            builder = NotificationCompat.Builder(context).apply {
                setContentTitle(title)
                setContentText(message)
                setSmallIcon(R.mipmap.ic_launcher_squircle)
                setStyle(NotificationCompat.BigTextStyle().bigText(message))
                setContentIntent(pendingIntent)
                setAutoCancel(true)
            }
        }

        notificationManager.notify(9, builder.build())
    }
}