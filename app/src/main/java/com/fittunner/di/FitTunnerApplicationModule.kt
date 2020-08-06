package com.fittunner.di

import com.fittunner.room.RunHistory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object FitTunnerApplicationModule {

    @Singleton
    @Provides
    fun provideRunHistoryDatabase()=RunHistory()
}