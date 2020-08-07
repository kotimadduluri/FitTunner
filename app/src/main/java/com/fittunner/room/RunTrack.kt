package com.fittunner.room

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class RunTrack(
    var screenshot: Bitmap?=null, //map image
    var avgSpeedKMH: Float=0f, //in kilo meters per hour
    var distance: Int=0,  //in meters
    var caloriesBurned: Int=0, //energy used
    var totalTimeTaken: Long=0L, //run time
    var createdAt: Long=0L
){
    @PrimaryKey(autoGenerate = true)
    var id: Int?=null
}