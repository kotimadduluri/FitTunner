package com.fittunner.util

import android.location.Location
import com.fittunner.service.Polyline

object TrackingUtility {

    fun calculatePolylineLength(polyline: Polyline):Float{
        var distence=0f
        for (i in 0..polyline.size-2){
            val pos1=polyline[i]
            val pos2=polyline[i+1]

            val result=FloatArray(1)

            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )

            distence+=result[0]
        }

        return distence
    }
}