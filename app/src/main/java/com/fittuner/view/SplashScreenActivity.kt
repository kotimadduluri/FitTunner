package com.fittuner.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.fittuner.BuildConfig
import com.fittuner.R
import com.fittuner.data.Constants.SPLASH_DISPLAY_DURATION
import com.fittuner.repository.SessionRepository
import com.fittuner.view.home.HomeActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
@Inject lateinit var sessionRepository: SessionRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        CoroutineScope(Dispatchers.Main).launch {
            delay(SPLASH_DISPLAY_DURATION)
         //   checkAdAndStartActivity()
            startTargetedActivity()
        }

        setupInterstitialAd();
    }

    private lateinit var mInterstitialAd: InterstitialAd
    fun setupInterstitialAd(){
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = BuildConfig.INTERSTITIAL_UNIT_ID
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }

    fun checkAdAndStartActivity(){
        if(mInterstitialAd.isLoaded){
            mInterstitialAd.show()
            mInterstitialAd.adListener = object : AdListener() {
                override fun onAdClosed() {
                    //mInterstitialAd.loadAd(AdRequest.Builder().build())
                    startTargetedActivity()
                }
            }
        }else{
            startTargetedActivity()
        }
    }

    fun startTargetedActivity(){
        /*var p1: Pair<View, String> = Pair.create(rootView, getString(R.string.logo))
        val p2: Pair<View, String> = Pair.create(rootView, getString(R.string.body))
        if(sessionRepository.isProfileUpdated()){
            startActivity(Intent(this, HomeActivity::class.java), ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                p1
            ).toBundle())
        }else{
            p1= Pair.create(logo, getString(R.string.logo))
            startActivity(Intent(this, InitialSetupActivity::class.java), ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                p1,p2
            ).toBundle())
        }*/

        startActivity(if(sessionRepository.isProfileUpdated()){
            Intent(this, HomeActivity::class.java)
        }else{
            Intent(this, InitialSetupActivity::class.java)
        } )

        finish()
    }
}