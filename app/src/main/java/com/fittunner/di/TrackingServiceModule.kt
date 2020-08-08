package com.fittunner.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.fittunner.MainActivity
import com.fittunner.R
import com.fittunner.data.Constants
import com.fittunner.view.home.HomeActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object TrackingServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext app:Context)= FusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(@ApplicationContext app:Context) = PendingIntent.getActivity(
        app,
        0,
        Intent(app, HomeActivity::class.java).also {
            it.action = Constants.ACTION_TRACKING
        }, PendingIntent.FLAG_UPDATE_CURRENT
    )

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(@ApplicationContext app:Context,pendingIntent: PendingIntent)=
        NotificationCompat.Builder(app, Constants.NOTIFICATIONS_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_run)
        .setContentTitle("FitTunning")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)
}