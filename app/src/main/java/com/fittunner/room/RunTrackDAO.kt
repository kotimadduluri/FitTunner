package com.fittunner.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunTrackDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(runTrack:RunTrack)

    @Delete
    suspend fun deleteRun(runTrack:RunTrack)

    @Query("SELECT * FROM running_table ORDER BY createdAt")
    fun getAllRunsBySortByDate():LiveData<List<RunTrack>>

}