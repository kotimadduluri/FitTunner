package com.fittuner.view.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.fittuner.BuildConfig
import com.fittuner.R
import com.fittuner.data.Constants.ACTION_TRACKING
import com.fittuner.view.tracking.MapTrackingActivity
import com.google.android.gms.ads.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        setContentView(R.layout.activity_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_settings
            )
        )
       // setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        handleIntent()
        setupInterstitialAd();
    }
    private lateinit var mInterstitialAd: InterstitialAd
    fun setupInterstitialAd(){
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = BuildConfig.INTERSTITIAL_UNIT_ID
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }


    private fun handleIntent() {
        //ACTION_TRACKING
        intent.action?.let {action->
            if(action==ACTION_TRACKING){
                startActivity(Intent(this,MapTrackingActivity::class.java))
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id){
            R.id.newRun->{
              //  checkAdAndStartActivity()
                startNewRun()
            }
        }
    }

    fun checkAdAndStartActivity(){
        if(mInterstitialAd.isLoaded){
            mInterstitialAd.show()
            mInterstitialAd.adListener = object : AdListener() {
                override fun onAdClosed() {
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                    startNewRun()
                }
            }
        }else{
            startNewRun()
        }
    }

    fun startNewRun(){
        startActivity(Intent(this,MapTrackingActivity::class.java))
    }

}