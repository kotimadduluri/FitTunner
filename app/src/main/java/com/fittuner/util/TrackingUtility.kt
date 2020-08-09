package com.fittuner.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Build
import com.fittuner.data.Constants.REQUEST_LOCATION_PERMISSIONS
import com.fittuner.service.Polyline
import pub.devrel.easypermissions.EasyPermissions

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

    fun hasLocationPermissions(context: Context)=
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }else{
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun requestLocationPermissions(context: Activity,request:Int=REQUEST_LOCATION_PERMISSIONS) {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                context,
                "You need to accept location permissions to use this application.",
                request,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }else{
            EasyPermissions.requestPermissions(
                context,
                "You need to accept location permissions to use this application.",
                request,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }
}

