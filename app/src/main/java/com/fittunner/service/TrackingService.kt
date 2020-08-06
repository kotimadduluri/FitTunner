package com.fittunner.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.fittunner.MainActivity
import com.fittunner.R
import com.fittunner.data.Constants.ACTION_TRACKING_FRAGMENT
import com.fittunner.data.Constants.ACTION_TRACK_PAUSE
import com.fittunner.data.Constants.ACTION_TRACK_START_OR_RESUME
import com.fittunner.data.Constants.ACTION_TRACK_STOP
import com.fittunner.data.Constants.FASTEST_LOCATION_INTERVAL
import com.fittunner.data.Constants.LOCATION_INTERVAL
import com.fittunner.data.Constants.NOTIFICATIONS_CHANNEL_ID
import com.fittunner.data.Constants.NOTIFICATIONS_CHANNEL_NAME
import com.fittunner.data.Constants.NOTIFICATION_ID
import com.fittunner.util.FitTunnerLogger
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng

/**
 * @author Koti madduluri
 * @Description service class to track user location and cache it
 */

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {

    val isStarted = false

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        fun sendCommand(context: Context, command: String) {
            Intent(context, TrackingService::class.java).also {
                it.action = command
                context.startService(it)
            }
        }

        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()

    }

    fun postIntialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postIntialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        FitTunnerLogger.e("id $startId")
        intent?.let {
            when (it.action) {
                ACTION_TRACK_START_OR_RESUME -> {
                    if (isStarted) {
                        FitTunnerLogger.e("Service resuming")
                    } else {
                        FitTunnerLogger.d("Service starting")
                        startForegroundService()
                    }
                }
                ACTION_TRACK_PAUSE -> FitTunnerLogger.d("Pause")
                ACTION_TRACK_STOP -> FitTunnerLogger.d("Stop")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))


    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        FitTunnerLogger.d("${location.latitude},${location.longitude}")
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {  //add call backs
            val request = LocationRequest().apply {
                interval = LOCATION_INTERVAL
                fastestInterval = FASTEST_LOCATION_INTERVAL
                priority = PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {  //to remove call backs
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(it.latitude, it.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    //notifications

    fun startForegroundService() {
//add empty line
        addEmptyPolyline()
        isTracking.postValue(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //checking device version then create channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("FitTunning")
            .setContentText("00:00:00")
            .setContentIntent(getPendingIntent())


        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun getPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_TRACKING_FRAGMENT
        }, FLAG_UPDATE_CURRENT
    )


    //notification channel
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATIONS_CHANNEL_ID,
            NOTIFICATIONS_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}