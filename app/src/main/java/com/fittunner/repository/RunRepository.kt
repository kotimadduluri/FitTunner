package com.fittunner.repository

import com.fittunner.room.RunTrack
import com.fittunner.room.RunTrackDAO
import javax.inject.Inject

class RunRepository @Inject constructor(val runTrackDAO: RunTrackDAO) {

    suspend fun insertRun(runTrack: RunTrack)=runTrackDAO.insertRun(runTrack)

    suspend fun deleteRun(runTrack: RunTrack)=runTrackDAO.deleteRun(runTrack)

    fun getAllRunsBySortByDate()=runTrackDAO.getAllRunsBySortByDate()
}