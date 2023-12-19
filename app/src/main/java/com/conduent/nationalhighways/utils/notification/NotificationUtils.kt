package com.conduent.nationalhighways.utils.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.conduent.nationalhighways.BuildConfig
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.common.Constants

class NotificationUtils(val context: Context) {

    private var channelId = BuildConfig.APPLICATION_ID
    lateinit var notificationManager: NotificationManager
    lateinit var intent: Intent
    lateinit var builder: Notification.Builder
    lateinit var notificationChannel: NotificationChannel

    public fun showNotification(title:String,message:String,type:String){
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(type==Constants.GEO_FENCE_NOTIFICATION){
            intent=Intent(context,LandingActivity::class.java)
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
            Log.e("TAG", "showNotification: " )
            notificationChannel =
                NotificationChannel(channelId, message, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
            builder = Notification.Builder(context, channelId).apply {
                setContentTitle(title)
                setContentText(message)
                setSmallIcon(R.drawable.ic_app_icon)
                setContentIntent(pendingIntent)
                setColor(context.resources.getColor(R.color.black, null))
                setAutoCancel(true)
            }
        } else {
            @Suppress("DEPRECATION")
            builder = Notification.Builder(context).apply {
                setContentTitle(title)
                setContentText(message)
                setSmallIcon(R.drawable.ic_app_icon)
                setContentIntent(pendingIntent)
                setAutoCancel(true)
            }
        }

        notificationManager.notify(9, builder.build())
    }
}