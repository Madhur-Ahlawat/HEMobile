package com.conduent.nationalhighways.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DartChargeFCMService : FirebaseMessagingService(), LifecycleObserver {

    private var isAppInForeground: Boolean = false
    private val message = "message"

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("TAG", "onMessageReceived() called with: remoteMessage = $remoteMessage")
        try {
            remoteMessage.notification?.let {
                it.body?.let { body -> sendNotification(body) }
            }
          /*  val from = remoteMessage.from
            val data: Map<*, *> = remoteMessage.data
            data[message]?.let {
                // check data and show notification
            }*/
        } catch (e: Exception) {
            // handle exception
        }
    }

    override fun onNewToken(token: String) {
        Log.e("TAG", "onNewToken() called with: token = $token")
        super.onNewToken(token)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        isAppInForeground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackground() {
        isAppInForeground = false
    }


    private fun sendNotification(messageBody: String) {
        val requestCode = 0
        val intent = Intent(this, LandingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_dart_charge_app_icon)
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}


