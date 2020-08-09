package com.fittuner.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.room.Room
import com.fittuner.data.Constants.APP_DATABASE_NAME
import com.fittuner.repository.APP_PREFERENCE_NAME
import com.fittuner.room.FitTunnerDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object FitTunnerApplicationModule {

    @Singleton
    @Provides
    fun provideFitTunnerDataBase(@ApplicationContext app: Context)=
        Room.databaseBuilder(
            app,
            FitTunnerDataBase::class.java,
            APP_DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun provideRunTrackDao(db:FitTunnerDataBase)=db.getRunTrackDao()

    @Singleton
    @Provides
    fun provideSharePreference(
        @ApplicationContext app:Context
    )=app.getSharedPreferences(APP_PREFERENCE_NAME,MODE_PRIVATE)


}