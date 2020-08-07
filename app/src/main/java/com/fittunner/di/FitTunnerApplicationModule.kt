package com.fittunner.di

import android.content.Context
import androidx.room.Room
import com.fittunner.data.Constants.APP_DATABASE_NAME
import com.fittunner.room.FitTunnerDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object FitTunnerApplicationModule {

    /*@Singleton
    @Provides
    fun provideRunHistoryDatabase()=RunHistory()*/

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

}