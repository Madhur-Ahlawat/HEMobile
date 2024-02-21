package com.conduent.nationalhighways.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.notification.NotificationUtils
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "GeofenceBroadcastReceiv"
    lateinit var notificationUtils: NotificationUtils
    lateinit var sessionManager: SessionManager
    override fun onReceive(context: Context, intent: Intent) {
        Log.e(TAG, "geofenceTransition- receiver -> ")
        sessionManager = SessionManager(Utils.returnSharedPreference(context))
        var checkLocationPermission = false
        var checkNotificationPermission = false
        if (sessionManager.fetchBooleanData(SessionManager.SettingsClick)) {
            checkLocationPermission = Utils.checkLocationpermission(context)
        } else {
            checkLocationPermission =
                (sessionManager.fetchBooleanData(SessionManager.LOCATION_PERMISSION) && Utils.checkLocationpermission(
                    context
                ))
        }

        checkNotificationPermission = if(Utils.areNotificationsEnabled(context)){
            sessionManager.fetchBooleanData(SessionManager.NOTIFICATION_PERMISSION)
        }else{
            false
        }
        Log.e(TAG,
            "onReceive: checkLocationPermission-> $checkLocationPermission checkNotificationPermission-> $checkNotificationPermission"
        )



        if (checkLocationPermission && checkNotificationPermission) {
            notificationUtils = NotificationUtils(context)
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            if (geofencingEvent?.hasError() == true) {
                val errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
                return
            }


            // Get the transition type.
            val geofenceTransition = geofencingEvent?.geofenceTransition
            Log.e(TAG, "geofenceTransition- geofenceTransition -> $geofenceTransition")
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                val dateFormat =
                    SimpleDateFormat(Constants.dd_mm_yyyy_hh_mm_ss, Locale.getDefault())
                val dateString = dateFormat.format(Date())
                sessionManager.saveStringData(SessionManager.GEOFENCE_ENTER_TIME, dateString)
                Toast.makeText(context, "Location entered", Toast.LENGTH_SHORT).show()
            }

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.e(
                    TAG,
                    "geofenceTransition- geofenceTransition -> " + sessionManager.fetchStringData(
                        SessionManager.GEOFENCE_ENTER_TIME
                    )
                )

                val geofenceEnterTime = Utils.convertStringToDate(
                    sessionManager.fetchStringData(SessionManager.GEOFENCE_ENTER_TIME),
                    Constants.dd_mm_yyyy_hh_mm_ss
                )
                if (geofenceEnterTime != null) {
                    if (sessionManager.fetchStringData(SessionManager.GEOFENCE_ENTER_TIME)
                            .isEmpty()
                    ) {
                        notificationUtils.showNotification(
                            context.resources.getString(R.string.str_did_you_cross_today),
                            context.resources.getString(R.string.str_responsible_paying),
                            Constants.GEO_FENCE_NOTIFICATION
                        )
                        Toast.makeText(context, "Location exit", Toast.LENGTH_SHORT).show()
                    } else {
                        val diff = Utils.getMinSecTimeDifference(geofenceEnterTime, Date())
                        Log.e(TAG, "onReceive: diff $diff")
                        if ( diff.first >= 5 && diff.second>0) {
                            Toast.makeText(
                                context,
                                "Location time limited more than 5 minutes",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            notificationUtils.showNotification(
                                context.resources.getString(R.string.str_did_you_cross_today),
                                context.resources.getString(R.string.str_responsible_paying),
                                Constants.GEO_FENCE_NOTIFICATION
                            )
                            Toast.makeText(context, "Location exit", Toast.LENGTH_SHORT).show()
                        }
                    }
                    sessionManager.saveStringData(SessionManager.GEOFENCE_ENTER_TIME, "")
                } else {
                    notificationUtils.showNotification(
                        context.resources.getString(R.string.str_did_you_cross_today),
                        context.resources.getString(R.string.str_responsible_paying),
                        Constants.GEO_FENCE_NOTIFICATION
                    )
                    Toast.makeText(context, "Location exit", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Log the error.
                Log.e(TAG, "geofenceTransition- error -> $geofenceTransition")
            }
        }else{
            Toast.makeText(
                context,
                "Location permission is " + checkLocationPermission +" Notification premission is "+checkNotificationPermission,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}