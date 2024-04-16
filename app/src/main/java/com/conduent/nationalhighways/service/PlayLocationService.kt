package com.conduent.nationalhighways.service

import android.Manifest
import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.location.Location
import android.location.LocationListener
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.gson.Gson

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

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand: playLocation")
        if (intent != null) {
            try {
                createLocationRequest()
                startLocationUpdates()
                try {
                    Log.e(TAG, "onCreate: foregroundservice")

                    ServiceCompat.startForeground(
                        this,  Constants.FOREGROUND_SERVICE_NOTIFICATIONID, createNotification(),
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            FOREGROUND_SERVICE_TYPE_LOCATION
                        } else {
                            0
                        },
                    )

                } catch (e: Exception) {
                    Log.e(TAG, "onCreate: e.message " + e.message)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                        && e is ForegroundServiceStartNotAllowedException
                    ) {
                        // App not in a valid state to start foreground service
                        // (e.g. started from bg)
                    } else if (e is NullPointerException) {

                    }

                }
            } catch (e: Exception) {
                if (e is NullPointerException) {

                }
            }
        }

        return START_STICKY
    }

    private fun createNotification(): Notification {
        val CHANNEL_ID = "my_channel_01"
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID,
                applicationContext.resources.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        channel.setSound(null, null)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(applicationContext.resources.getString(R.string.app_name))
            .setSmallIcon(R.drawable.notification_icon)
            .setColor(resources.getColor(R.color.red, null))
            .setContentText(applicationContext.resources.getString(R.string.dartcharge_running))
            .build()
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


    override fun onTaskRemoved(rootIntent: Intent) {
        Utils.writeInFile(null,"PlayLocation Task Removed")
        super.onTaskRemoved(rootIntent)
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
            broadcaster?.sendBroadcast(ii)
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


