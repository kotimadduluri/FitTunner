package com.fittunner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fittunner.room.RunHistory
import com.fittunner.util.FitTunnerLogger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var runHistory:RunHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FitTunnerLogger.d("${runHistory.hashCode()}")
    }
}