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
import com.fittunner.R
import com.fittunner.data.Constants.ACTION_TRACK_PAUSE
import com.fittunner.data.Constants.ACTION_TRACK_START_OR_RESUME
import com.fittunner.data.Constants.ACTION_TRACK_STOP
import com.fittunner.data.Constants.FASTEST_LOCATION_INTERVAL
import com.fittunner.data.Constants.LOCATION_INTERVAL
import com.fittunner.data.Constants.NOTIFICATIONS_CHANNEL_ID
import com.fittunner.data.Constants.NOTIFICATIONS_CHANNEL_NAME
import com.fittunner.data.Constants.NOTIFICATION_ID
import com.fittunner.util.FitTunnerLogger
import com.fittunner.util.TimeUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Koti madduluri
 * @Description service class to track user location and cache it
 */

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    var isStarted = false
    var isServiceKilled = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var currentNotificationBuilder: NotificationCompat.Builder


    private val timeRunInSeconds = MutableLiveData<Long>()

    companion object {
        fun sendCommand(context: Context, command: String) {
            Intent(context, TrackingService::class.java).also {
                it.action = command
                context.startService(it)
            }
        }

        val timeRunInMills = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()

    }

    fun postIntialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInMills.postValue(0L)
        timeRunInSeconds.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        currentNotificationBuilder = baseNotificationBuilder
        postIntialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        FitTunnerLogger.e("id $startId")
        intent?.let {
            when (it.action) {
                ACTION_TRACK_START_OR_RESUME -> {
                    if (isStarted) { //resume
                        startTimer()
                        FitTunnerLogger.e("Service resuming")
                    } else {
                        isStarted = true
                        FitTunnerLogger.d("Service starting")
                        startForegroundService()
                    }
                }
                ACTION_TRACK_PAUSE -> {
                    FitTunnerLogger.d("Pause")
                    pauseService()
                }
                ACTION_TRACK_STOP -> {
                    FitTunnerLogger.d("Stop")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun killService() {
        isServiceKilled = true
        isStarted = false
        pauseService()
        postIntialValues()
        stopForeground(true)
        stopSelf()
    }

    private var isTimerEnabled = false
    private var lapTime = 0L //laps
    private var timeRun = 0L  //total time some of of laps
    private var timestarted = 0L
    private var lastSecondTimestamp = 0L

    fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timestarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                //time diff betwwen now and timestarted
                lapTime = System.currentTimeMillis() - timestarted
                //post new laptime
                timeRunInMills.postValue(timeRun + lapTime)
                if (timeRunInMills.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }

                delay(50L)
            }
            timeRun += lapTime
        }
    }

    fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
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

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val actionText = if (isTracking) "Pause" else "Resume"

        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_TRACK_PAUSE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_TRACK_START_OR_RESUME
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        if(!isServiceKilled){
            currentNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause, actionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
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
        startTimer()
        isTracking.postValue(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //checking device version then create channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        startForeground(
            NOTIFICATION_ID,
            baseNotificationBuilder.build()
        )

        timeRunInSeconds.observe(this, Observer {
            if(!isServiceKilled){
                val notification = currentNotificationBuilder
                    .setContentText(
                        TimeUtility.getFormattedStopWatchTime(it * 1000L)
                    )
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        })
    }

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