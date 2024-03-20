package com.conduent.nationalhighways.receiver

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.notification.NotificationUtils
import com.google.android.gms.location.Geofence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class GeofenceWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {
    lateinit var notificationUtils: NotificationUtils

    override fun doWork(): Result {
        // Implement geofence handling logic here
        // This method runs on a background thread
       val sessionManager = SessionManager(Utils.returnSharedPreference(context))
        notificationUtils = NotificationUtils(context)

        val geofenceTransition = inputData.getInt("geofenceTransition", -1)
        val requestID = inputData.getString("geofenceId")
        Log.e("TAG", "doWork: geofenceTransition  $geofenceTransition requestID $requestID" )
        val dateFormat =
            SimpleDateFormat(Constants.dd_mm_yyyy_hh_mm_ss, Locale.getDefault())
        val dateString = dateFormat.format(Date())


        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            if (requestID == Constants.geofenceSouthBoundDartCharge) {
                sessionManager.saveStringData(
                    SessionManager.GEOFENCE_SOUTHBOUND_ENTER_TIME,
                    dateString
                )
            } else {
                sessionManager.saveStringData(
                    SessionManager.GEOFENCE_NORTHBOUND_ENTER_TIME,
                    dateString
                )
            }
            showToastMessage(context,"Location entered")
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            showToastMessage(context,"Location exit")
            var geofenceEnterTime: Date? = null

            val geofenceNorthBoundEnterTime =
                sessionManager.fetchStringData(SessionManager.GEOFENCE_NORTHBOUND_ENTER_TIME)
            val geofenceSouthBoundEnterTime =
                sessionManager.fetchStringData(SessionManager.GEOFENCE_SOUTHBOUND_ENTER_TIME)


            if (sessionManager.fetchStringData(SessionManager.GEOFENCE_NORTHBOUND_EXIT_TIME)
                    .isNotEmpty()
            ) {
                Log.e("TAG", "onReceive: ")
                geofenceEnterTime = Utils.convertStringToDate1(
                    sessionManager.fetchStringData(SessionManager.GEOFENCE_NORTHBOUND_ENTER_TIME),
                    Constants.dd_mm_yyyy_hh_mm_ss
                )
            } else {
                Log.e("TAG", "onReceive: 11 ")
                geofenceEnterTime = Utils.convertStringToDate1(
                    sessionManager.fetchStringData(SessionManager.GEOFENCE_SOUTHBOUND_ENTER_TIME),
                    Constants.dd_mm_yyyy_hh_mm_ss
                )
            }

            if (geofenceNorthBoundEnterTime.isNotEmpty() && geofenceSouthBoundEnterTime.isNotEmpty() && geofenceEnterTime != null) {

                val diff = Utils.getMinSecTimeDifference(geofenceEnterTime, Date())
                Log.e("TAG", "onReceive: diff $diff")
                if (diff.first >= 30 && diff.second > 0) {
                    showToastMessage(context,"Location time limited more than 30 minutes")
                } else {
                    notificationUtils.showNotification(
                        context.resources.getString(R.string.str_did_you_cross_today),
                        context.resources.getString(R.string.str_responsible_paying),
                        Constants.GEO_FENCE_NOTIFICATION
                    )
                    showToastMessage(context,"Location exit")
                }

                sessionManager.saveStringData(
                    SessionManager.GEOFENCE_NORTHBOUND_ENTER_TIME,
                    ""
                )
                sessionManager.saveStringData(
                    SessionManager.GEOFENCE_SOUTHBOUND_ENTER_TIME,
                    ""
                )
                sessionManager.saveStringData(
                    SessionManager.GEOFENCE_NORTHBOUND_EXIT_TIME,
                    ""
                )
                sessionManager.saveStringData(
                    SessionManager.GEOFENCE_SOUTHBOUND_EXIT_TIME,
                    ""
                )

            }

            if (requestID == Constants.geofenceNorthBoundDartCharge) {
                sessionManager.saveStringData(
                    SessionManager.GEOFENCE_NORTHBOUND_EXIT_TIME,
                    dateString
                )
            } else {
                sessionManager.saveStringData(
                    SessionManager.GEOFENCE_SOUTHBOUND_EXIT_TIME,
                    dateString
                )
            }

        } else {
            // Log the error.
            Log.e("TAG", "geofenceTransition- error -> $geofenceTransition")
        }

        return Result.success()
    }

    fun showToastMessage(context: Context,message:String){
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}