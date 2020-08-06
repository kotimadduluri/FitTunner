package com.fittunner.util

import java.util.concurrent.TimeUnit

object TimeUtility {

    fun getFormattedStopWatchTime(ms:Long,includeMills:Boolean=false):String{
        var milliseconds=ms;

        val hours=TimeUnit.MILLISECONDS.toHours(milliseconds)

        milliseconds-=TimeUnit.HOURS.toMillis(hours)
        val minutes=TimeUnit.MILLISECONDS.toMinutes(milliseconds)

        milliseconds-=TimeUnit.MINUTES.toMillis(minutes)
        val seconds=TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        if(!includeMills){
            return "${if(hours<10)"0" else ""}$hours:"+
                    "${if(minutes<10)"0" else ""}$minutes:"+
                    "${if(seconds<10)"0" else ""}$seconds"
        }

        milliseconds-=TimeUnit.SECONDS.toMillis(seconds)
        milliseconds/=10

        return "${if(hours<10) "0" else "" }$hours:"+
                "${if(minutes<10) "0" else "" }$minutes:"+
                "${if(seconds<10) "0" else "" }$seconds:"+
                "${if(milliseconds<10) "0" else "" }$milliseconds"

    }

}