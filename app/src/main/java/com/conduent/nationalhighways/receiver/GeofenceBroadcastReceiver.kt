package com.conduent.nationalhighways.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.notification.NotificationUtils
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private  val TAG = "GeofenceBroadcastReceiv"
    lateinit var notificationUtils: NotificationUtils
    override fun onReceive(context: Context, intent: Intent) {
        Log.e(TAG, "geofenceTransition- receiver -> ")
        notificationUtils= NotificationUtils(context)
        val geofencingEvent =  GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent?.geofenceTransition
        Log.e(TAG, "geofenceTransition- geofenceTransition -> "+geofenceTransition)

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition==Geofence.GEOFENCE_TRANSITION_EXIT) {
            notificationUtils.showNotification(
                context.resources.getString(R.string.str_did_you_cross_today),
                context.resources.getString(R.string.str_responsible_paying),
                Constants.GEO_FENCE_NOTIFICATION
                )
        } else {
            // Log the error.
            Log.e(TAG, "geofenceTransition- error -> "+ geofenceTransition)
        }
    }
}