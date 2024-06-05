package com.conduent.nationalhighways.receiver

import android.app.ForegroundServiceStartNotAllowedException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.conduent.nationalhighways.service.PlayLocationService
import com.conduent.nationalhighways.utils.GeofenceUtils
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class BootReceiver : BroadcastReceiver() {
    private val TAG = "BootReceiver"
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
                startForeService(context)
            }

            Intent.ACTION_BOOT_COMPLETED -> {
                // Re-register geofences on device boot
                startForeService(context)
                context.let { GeofenceUtils.startGeofence(it, 1) }
            }

            Intent.ACTION_PACKAGE_ADDED -> {
                // Re-register geofences on app install
                val packageName = intent.data?.schemeSpecificPart
                if (packageName == context.packageName) {
                    startForeService(context)
                    GeofenceUtils.startGeofence(context, 2)
                }
            }
        }
    }


    private fun startForeService(context:Context){
        Utils.writeInFile(context, "BootReceiver startForeService Called")
        val serviceIntent = Intent(Intent.ACTION_MAIN).setClass(context, PlayLocationService::class.java)
        serviceIntent.putExtra("StartForeground", true)

        if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.S) {
            try {
                Utils.writeInFile(context, "BootReceiver S startForeService")
                context.startForegroundService(serviceIntent)
            } catch (fssnae: ForegroundServiceStartNotAllowedException) {
                Utils.writeInFile(context, "BootReceiver startForeService exception 1 ->"+fssnae.message)
                Log.e(TAG,"[Api31 Compatibility] Can't start service as foreground! $fssnae")
            } catch (se: SecurityException) {
                Utils.writeInFile(context, "BootReceiver startForeService exception 2 ->"+se.message)
                Log.e(TAG,"[Api31 Compatibility] Can't start service as foreground! $se")
            } catch (e: Exception) {
                Utils.writeInFile(context, "BootReceiver startForeService exception 3 ->"+e.message)
                Log.e(TAG,"[Api31 Compatibility] Can't start service as foreground! $e")
            }
        }else {
            Utils.writeInFile(context, "BootReceiver O startForeService")
            context.startForegroundService(serviceIntent)
        }

    }


}

