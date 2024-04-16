package com.conduent.nationalhighways.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.conduent.nationalhighways.utils.GeofenceUtils
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class BootReceiver : BroadcastReceiver() {

    lateinit var sessionManager: SessionManager

    override fun onReceive(context: Context, intent: Intent?) {
        sessionManager = SessionManager(Utils.returnSharedPreference(context))
        Utils.writeInFile(context, "BootReceiver Called")
        Utils.writeInFile(context, "BootReceiver action is -- " + intent?.action)
        when (intent?.action) {
            Intent.ACTION_LOCKED_BOOT_COMPLETED -> {
                FirebaseApp.initializeApp(context)
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }
                    // Get new FCM registration token
                    sessionManager.setFirebasePushToken(task.result)
                    Log.i("PUSHTOKENTAG", "Receiver firebase token is : ${task.result}")
                })
            }

            Intent.ACTION_BOOT_COMPLETED -> {
                // Re-register geofences on device boot
                context.let { GeofenceUtils.startGeofence(it, 1) }
            }

            Intent.ACTION_PACKAGE_ADDED -> {
                // Re-register geofences on app install
                val packageName = intent.data?.schemeSpecificPart
                if (packageName == context.packageName) {
                    if (context != null) {
                        GeofenceUtils.startGeofence(context, 2)
                    }
                }
            }
        }
    }

}

