package com.conduent.nationalhighways.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.conduent.nationalhighways.utils.common.SessionManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class BootCompleteReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_LOCKED_BOOT_COMPLETED)) {

            FirebaseApp.initializeApp(context)
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                sessionManager.setFirebasePushToken(task.result)
                Log.i("teja1234", "Receiver firebase token is : ${task.result}")
            })
        }
    }
}