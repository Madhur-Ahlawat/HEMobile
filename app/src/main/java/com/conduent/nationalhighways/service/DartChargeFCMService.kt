package com.conduent.nationalhighways.service

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
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
        try {
            val from = remoteMessage.from
            val data: Map<*, *> = remoteMessage.data
            data[message]?.let {
                // check data and show notification
            }
        } catch (e: Exception) {
            // handle exception
        }
    }

    override fun onNewToken(token: String) {
        // authenticate with new token
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        isAppInForeground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackground() {
        isAppInForeground = false
    }


}


