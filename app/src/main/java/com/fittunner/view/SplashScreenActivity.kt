package com.fittunner.view

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.fittunner.R
import com.fittunner.view.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashScreenActivity : AppCompatActivity() {
    val SPLASH_DISPLAY_DURATION=3000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        CoroutineScope(Dispatchers.Main).launch {
            delay(SPLASH_DISPLAY_DURATION)
            startHomeActivity()
        }
    }

    fun startHomeActivity(){
        val mainIntent = Intent(this, HomeActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}