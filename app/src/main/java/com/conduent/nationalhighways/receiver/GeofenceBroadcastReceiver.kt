package com.conduent.nationalhighways.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.Date

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "GeofenceBroadcastReceiv"
    lateinit var sessionManager: SessionManager
    override fun onReceive(context: Context, intent: Intent) {
        Log.e(TAG, "geofenceTransition - receiver -> ")
        sessionManager = SessionManager(Utils.returnSharedPreference(context))

        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(directory, "dartlogs_20_1.txt")
        if (file.exists()) {
            val fileWriter = FileWriter(file, true)
            val bufferedWriter = BufferedWriter(fileWriter)

            bufferedWriter.write("Geofence broadcast broadcast receiver triggered " + Date().toString() + "\n")
            bufferedWriter.newLine()
            checkNotification(sessionManager, context, intent, bufferedWriter)
        } else {
            checkNotification(sessionManager, context, intent, null)
        }


    }

    private fun checkNotification(
        sessionManager: SessionManager,
        context: Context,
        intent: Intent,
        bufferedWriter: BufferedWriter?
    ) {
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

        checkNotificationPermission = if (Utils.areNotificationsEnabled(context)) {
            sessionManager.fetchBooleanData(SessionManager.NOTIFICATION_PERMISSION)
        } else {
            false
        }
        Log.e(
            TAG,
            "onReceive: checkLocationPermission-> $checkLocationPermission checkNotificationPermission-> $checkNotificationPermission"
        )


        bufferedWriter?.write("checkLocationPermission $checkLocationPermission and checkNotificationPermission $checkNotificationPermission \n")
        bufferedWriter?.newLine()

        if (checkLocationPermission && checkNotificationPermission) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            geofencingEvent?.geofenceTransition
            if (geofencingEvent?.hasError() == true) {
                val errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
                return
            }


            // Get the transition type.
            val geofenceTransition = geofencingEvent?.geofenceTransition
            Log.e(TAG, "geofenceTransition- geofenceTransition -> $geofenceTransition")


            var requestID = ""

            // Iterate over triggered geofences
            for (geofence in geofencingEvent?.triggeringGeofences!!) {
                // Retrieve requestId and transition type
                requestID = geofence.requestId
            }

            bufferedWriter?.write("geofenceTransition $geofenceTransition requestID $requestID \n")
            bufferedWriter?.newLine()



            Log.e(TAG, "onReceive: requestID " + requestID)

        } else {
            Toast.makeText(
                context,
                "Location permission is " + checkLocationPermission + " Notification premission is " + checkNotificationPermission,
                Toast.LENGTH_SHORT
            ).show()
        }

        bufferedWriter?.close()
    }
}