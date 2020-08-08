package com.fittunner.view.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fittunner.MainActivity
import com.fittunner.R
import com.fittunner.data.Constants.ACTION_TRACKING
import com.fittunner.util.FitTunnerLogger
import com.fittunner.view.tracking.MapTrackingActivity
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
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
       // setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        handleIntent()
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
                startActivity(Intent(this,MapTrackingActivity::class.java))
            }
        }
    }
}