package com.fittunner.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunTrackDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(runTrack:RunTrack)

    @Delete
    suspend fun deleteRun(runTrack:RunTrack)

    @Query("SELECT * FROM running_table ORDER BY createdAt DESC")
    fun getAllRunsBySortByDate():LiveData<List<RunTrack>>

    @Query("SELECT * FROM running_table ORDER BY totalTimeTaken DESC")
    fun getAllRunsSortedByTimeInMillis(): LiveData<List<RunTrack>>

    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): LiveData<List<RunTrack>>

    @Query("SELECT * FROM running_table ORDER BY distance DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<RunTrack>>

    @Query("SELECT * FROM running_table ORDER BY avgSpeedKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): LiveData<List<RunTrack>>

    @Query("SELECT SUM(totalTimeTaken) FROM running_table")
    fun getTotalTimeInMillis(): LiveData<Long>

    @Query("SELECT SUM(distance) FROM running_table")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(avgSpeedKMH) FROM running_table")
    fun getTotalAvgSpeed(): LiveData<Float>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned(): LiveData<Long>

}