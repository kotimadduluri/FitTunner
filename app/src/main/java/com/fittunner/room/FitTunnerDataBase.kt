package com.fittunner.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fittunner.room.converters.BitmapConverter

@Database(
    entities = [RunTrack::class],
    version = 1
)
@TypeConverters(BitmapConverter::class)
abstract class FitTunnerDataBase :RoomDatabase(){
    abstract fun getRunTrackDao():RunTrackDAO
}