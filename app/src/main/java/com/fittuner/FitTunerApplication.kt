package com.fittuner

import android.app.Application
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class FitTunerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initGoogleAdds()
    }

    fun initGoogleAdds(){
            try{
                if(BuildConfig.DEBUG) {
                    val testDeviceIds = Arrays.asList(AdRequest.DEVICE_ID_EMULATOR)
                    val configuration =
                        RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
                    MobileAds.setRequestConfiguration(configuration)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
    }

}