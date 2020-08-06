package com.fittunner.util

import android.util.Log

object FitTunnerLogger {
    private const val TAG = "FitTunner::-->"
    fun e(message:String){
        Log.e(TAG,message)
    }

    fun d(message:String){
        Log.d(TAG,message)
    }
}