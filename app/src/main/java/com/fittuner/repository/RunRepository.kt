package com.fittuner.repository

import com.fittuner.room.RunTrack
import com.fittuner.room.RunTrackDAO
import javax.inject.Inject

class RunRepository @Inject constructor(val runTrackDAO: RunTrackDAO) {

    suspend fun insertRun(runTrack: RunTrack)=runTrackDAO.insertRun(runTrack)

    suspend fun deleteRun(runTrack: RunTrack)=runTrackDAO.deleteRun(runTrack)

    fun getTotalTimeInMillis()=runTrackDAO.getTotalTimeInMillis()

    fun getTotalDistance()=runTrackDAO.getTotalDistance()

    fun getTotalCaloriesBurned()=runTrackDAO.getTotalCaloriesBurned()

    fun getTotalAvgSpeed()=runTrackDAO.getTotalAvgSpeed()

    fun getAllRunsBySortByDate()=runTrackDAO.getAllRunsBySortByDate()

    fun getAllRuns()=runTrackDAO.getAllRuns()
}