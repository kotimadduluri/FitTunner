package com.fittuner.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val DATE_FRMAT="dd.MM.yyyy"

object TimeUtility {
    val SIMPLE_DATE_FORMAT=SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

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

    fun getTotalHoursFromMills(ms:Long,includeMills:Boolean=false):String{
        var milliseconds=ms

        val hours=TimeUnit.MILLISECONDS.toHours(milliseconds)

        milliseconds-=TimeUnit.HOURS.toMillis(hours)
        val minutes=TimeUnit.MILLISECONDS.toMinutes(milliseconds)

       /* milliseconds-=TimeUnit.MINUTES.toMillis(minutes)
        val seconds=TimeUnit.MILLISECONDS.toSeconds(milliseconds)
*/
        if(!includeMills){
            return "${if(hours<10)"0" else ""}$hours:"+
                    "${if(minutes<10)"0" else ""}$minutes"
        }

        return "${if(hours<10) "0" else "" }$hours:"+
                "${if(minutes<10) "0" else "" }$minutes"

    }

    fun getDate(timeStamp: Long): String {
        val calendar= Calendar.getInstance().apply {
            timeInMillis=timeStamp
        }
        return  SIMPLE_DATE_FORMAT.format(calendar.time)
    }
}