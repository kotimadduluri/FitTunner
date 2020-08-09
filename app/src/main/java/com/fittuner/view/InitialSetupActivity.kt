package com.fittuner.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.fittuner.R
import com.fittuner.repository.SessionRepository
import com.fittuner.view.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_initial_setup.*
import kotlinx.android.synthetic.main.activity_initial_setup.rootView
import javax.inject.Inject

@AndroidEntryPoint
class InitialSetupActivity : AppCompatActivity() {

    @Inject lateinit var sessionRepository: SessionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_setup)
        saveDetails.setOnClickListener {
            validateFormAndSave()
        }
        setupOldData()
    }

    private fun setupOldData(){
        if(sessionRepository.isProfileUpdated()){
            userName.setText(sessionRepository.getUserName())
            userWeight.setText("${sessionRepository.getUserWeight()}")
            saveDetails.text="Update"
        }
    }

    private fun validateFormAndSave() {
        val name= userName.text.toString().trim()
        val weight= userWeight.text.toString().trim()
        if(name.isEmpty()){
            showMessage("Please enter name")
        }else if(name.length<2){
            showMessage("Name length should be less than 2")
        }else if(weight.isEmpty()){
            showMessage("Please enter weight")
        }else if(weight.equals("0")){
            showMessage("Weight should not be 0")
        }else{
            sessionRepository.setUserName(name)
            sessionRepository.setUserWeight(weight.toLong())
            if(sessionRepository.isProfileUpdated()){
                onBackPressed()
            }else{
                sessionRepository.setProfile(true)
                startHome()
            }
        }
    }

    private fun startHome() {
        val p1: Pair<View, String> = Pair.create(rootView, getString(R.string.logo))
       // val p2: Pair<View, String> = Pair.create(rootView, getString(R.string.body))
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            p1
        )

        startActivity(
            Intent(this, HomeActivity::class.java),
            options.toBundle()
        )

        finish()

    }

    fun showMessage(message:String){
        Snackbar.make(
            userName,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}