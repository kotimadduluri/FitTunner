package com.fittuner.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fittuner.room.converters.BitmapConverter

@Database(
    entities = [RunTrack::class],
    version = 1
)
@TypeConverters(BitmapConverter::class)
abstract class FitTunnerDataBase :RoomDatabase(){
    abstract fun getRunTrackDao():RunTrackDAO
}