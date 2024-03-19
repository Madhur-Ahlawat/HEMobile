package com.conduent.nationalhighways.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.conduent.nationalhighways.utils.GeofenceUtils

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Re-register geofences on device boot
                context?.let { GeofenceUtils.startGeofence(it,1) }
            }
            Intent.ACTION_PACKAGE_ADDED -> {
                // Re-register geofences on app install
                val packageName = intent.data?.schemeSpecificPart
                if (packageName == context?.packageName) {
                    if (context != null) {
                        GeofenceUtils.startGeofence(context,2)
                    }
                }
            }
        }
    }

}

