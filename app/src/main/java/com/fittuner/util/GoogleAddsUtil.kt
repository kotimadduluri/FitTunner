package com.fittuner.util

import android.content.Context
import com.fittuner.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import java.lang.Exception

object GoogleAddsUtil {

    fun getBannerAdd(context: Context): AdView? {
        try{
            val adView = AdView(context)
            adView.adSize = AdSize.SMART_BANNER
            adView.adUnitId = BuildConfig.BANNER_UNIT_ID
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
            return adView
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }
}