package com.conduent.nationalhighways.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.*
import com.conduent.nationalhighways.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

class PlayLocationService : Service(), LocationListener {
    private val binder: IBinder = LocalBinder()
    var latitude = 0.0
    var longitude = 0.0
    var gson: Gson = Gson()
    var broadcaster: LocalBroadcastManager? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                try {
                    Log.e(TAG, "onLocationResult: ************$locationResult")
                    if (locationResult.equals(null)) {
                        return
                    }
                    for (location in locationResult.locations) {
                        updateLocation(location)
                    }

                } catch (e: Exception) {
                }
            }
        }
        try {
            Log.e(TAG, "onCreate: foregroundservice" )
//            foregroundService()
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: e.message "+e.message )
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand: playLocation")
        try {
            createLocationRequest()
            startLocationUpdates()
        } catch (e: Exception) {
        }
        return START_STICKY
    }

    private fun createLocationRequest() {
        try {
            mLocationRequest = LocationRequest.create().apply {
                interval = UPDATE_INTERVAL_IN_MILLISECONDS
                fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
                priority = Priority.PRIORITY_HIGH_ACCURACY
                isWaitForAccurateLocation = true
            }
        } catch (e: Exception) {
        }
    }

    private fun startLocationUpdates() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProviderClient!!.requestLocationUpdates(
                    mLocationRequest!!,
                    mLocationCallback!!,
                    Looper.myLooper()!!
                )
            } else if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProviderClient!!.requestLocationUpdates(
                    mLocationRequest!!,
                    mLocationCallback!!,
                    Looper.myLooper()!!
                )
            }
        } catch (e: Exception) {
        }
    }

    override fun onLocationChanged(location: Location) {
        Log.e(TAG, "onLocationChanged: ")
        updateLocation(location)
    }

    //updating location
    private fun updateLocation(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
        playlat = latitude
        playlong = longitude
        broadCastLocation(latitude.toString(), longitude.toString())
    }

    //running a foregroundService for continuously getting latLongs and app working in all states.
    private fun foregroundService() {
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"
            var channel = NotificationChannel(
                CHANNEL_ID,
                applicationContext.resources.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
            )
            channel.setSound(null, null)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            val notification = Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(applicationContext.resources.getString(R.string.dartcharge_running))
                .setSmallIcon(R.drawable.push_notification)
                .setColor(resources.getColor(R.color.blue, null))
                .setContentText(applicationContext.resources.getString(R.string.tap_for_more_info))
                .build()
            startForeground(123, notification)
        } else {
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.push_notification)
                .setContentTitle(applicationContext.resources.getString(R.string.dartcharge_running))
                .setContentText(applicationContext.resources.getString(R.string.tap_for_more_info))
            startForeground(123, builder.build())
        }
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    /**
     * Removes location updates From the FusedLocationApi.
     */
    private fun stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        fusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback!!)
    }

    //Local binder to bind the service and communicate with this LocationUpdateService class.
    inner class LocalBinder : Binder() {
        val service: PlayLocationService
            get() = this@PlayLocationService
    }

    private fun broadCastLocation(lat: String, lng: String) {
        broadcaster = LocalBroadcastManager.getInstance(applicationContext)
        if (broadcaster != null) {
            val ii = Intent("update")
            ii.putExtra("lat", lat)
            ii.putExtra("lng", lng)
            broadcaster!!.sendBroadcast(ii)
        }
    }

    companion object {
        val TAG = PlayLocationService::class.java.simpleName
        const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
        const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
        var playlat = 0.0
        var playlong = 0.0
    }
}


