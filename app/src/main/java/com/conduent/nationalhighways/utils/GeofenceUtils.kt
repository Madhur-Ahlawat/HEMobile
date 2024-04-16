package com.conduent.nationalhighways.utils


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.conduent.nationalhighways.receiver.GeofenceBroadcastReceiver
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter


@SuppressLint("StaticFieldLeak")
object GeofenceUtils {
    private val TAG = "GeofenceUtils"

    private val gadgetQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    private val geofenceList = ArrayList<Geofence>()

    lateinit var geoClient: GeofencingClient
    lateinit var geofenceCircularIntent: PendingIntent

    lateinit var context: Context

    //starting geofence
    fun startGeofence(context1: Context, from: Int = 0) {
        Utils.writeInFile(context1,"startgeofence from $from")
        context = context1
        val geofenceIntent: PendingIntent by lazy {
            val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
        geofenceCircularIntent = geofenceIntent
        geoClient = LocationServices.getGeofencingClient(context)

        removeGeofenceByIds()
        geofenceList.clear()
        geofenceList.add(
            Geofence.Builder().apply {
                setRequestId(Constants.geofenceNorthBoundDartCharge)
                setCircularRegion(
                    17.463311, 78.561253,
                    300f
                )
                setExpirationDuration(Geofence.NEVER_EXPIRE)
                setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            }.build()
        )
        geofenceList.add(
            Geofence.Builder().apply {
                setRequestId(Constants.geofenceSouthBoundDartCharge)
                setCircularRegion(
                    17.473612, 78.570747,
                    300f
                )
                setExpirationDuration(Geofence.NEVER_EXPIRE)
                setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            }.build()
        )

    }


    //adding a Geofence
    private fun addGeofence() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        geofenceCircularIntent.let {
            geoClient.addGeofences(seekGeofencing(geofenceList), it).run {
                Log.e(TAG, "addGeofence: Geofence added in utils:")
                addOnSuccessListener {
                    Log.e(TAG, "addGeofence: Geofences added successfully")
                }
                addOnFailureListener {
                    Log.e(TAG, "addGeofence: Geofence adding failed")
                }
            }
        }
    }

    //specify the Geofence to monitor and the initial trigger
    private fun seekGeofencing(list: ArrayList<Geofence>): GeofencingRequest {
        Log.e(TAG, "seekGeofencing: list:$list")
        return GeofencingRequest.Builder().apply {
//            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(list)
        }.build()
    }

    private fun removeGeofenceByIds() {
        Log.e(TAG, "removeGeofenceByIds : $geofenceList")
        val list = ArrayList<String>()
        list.add(Constants.geofenceSouthBoundDartCharge)
        list.add(Constants.geofenceNorthBoundDartCharge)
        geoClient.removeGeofences(list).run {
            addOnSuccessListener {
                Log.e(TAG, "removeGeofenceByIds : Circular fences removed ")
                examinePermissionAndInitiateGeofence()
            }
            addOnFailureListener {
                Log.e(TAG, "removeGeofenceByIds : Circular removing failed")
                examinePermissionAndInitiateGeofence()
            }
        }
    }

    private fun examinePermissionAndInitiateGeofence() {
        if (authorizedLocation()) {
            Log.e(TAG, "examinePermissionAndInitiateGeofence: auth")
            validateGadgetAreaInitiateGeofence()
        }
    }

    // check if background and foreground permissions are approved
    @TargetApi(29)
    private fun authorizedLocation(): Boolean {
        val formalizeForeground =
            (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        val coarseForeground =
            (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        val formalizeBackground =
            if (gadgetQ) {
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            } else {
                true
            }
        return (formalizeForeground || coarseForeground) && formalizeBackground
    }

    private fun validateGadgetAreaInitiateGeofence(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(context)
        val locationResponses =
            client.checkLocationSettings(builder.build())

        locationResponses.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    /*  if(context is HomeActivity){
                          exception.startResolutionForResult(
                              (context as HomeActivity) ,
                              REQUEST_TURN_DEVICE_LOCATION_ON
                          )
                      }else{
                      }*/
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                } catch (ex: Exception) {

                }
            } else {
                Toast.makeText(context, "Enable your location", Toast.LENGTH_SHORT).show()
            }
        }

        locationResponses.addOnCompleteListener {
            addGeofence()
        }

        locationResponses.addOnSuccessListener {
            Log.e(TAG, "addOnSuccessListener ")
        }
    }
}