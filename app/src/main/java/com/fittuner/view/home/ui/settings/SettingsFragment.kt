package com.fittuner.view.home.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.fittuner.R
import com.fittuner.util.GoogleAddsUtil
import com.fittuner.util.TimeUtility
import com.fittuner.view.InitialSetupActivity
import com.fittuner.view.home.ui.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.addContainer
import kotlinx.android.synthetic.main.fragment_settings.tvAverageSpeed
import kotlinx.android.synthetic.main.fragment_settings.tvTotalCalories
import kotlinx.android.synthetic.main.fragment_settings.tvTotalDistence
import kotlinx.android.synthetic.main.fragment_settings.tvTotalTime

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val dashbordViewModel: DashboardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editProfile.setOnClickListener {
            requireContext().startActivity(Intent(
                requireContext(),
                InitialSetupActivity::class.java
            ))
        }

        subscribeToObservers()
        initGoogleAdds()
    }

    override fun onResume() {
        super.onResume()
        setProfileData()
    }

    private fun setProfileData() {
        userName.text=settingsViewModel.sessionRepository.getUserName()
        userWeight.text="${settingsViewModel.sessionRepository.getUserWeight()} kgs"
    }

    private fun subscribeToObservers() {
        dashbordViewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            // in case DB is empty it will be null
            it?.let {
                val km = it / 1000f
                val totalDistance = Math.round(km * 10) / 10f
                val totalDistanceString = "${totalDistance}"
                tvTotalDistence.text = totalDistanceString
            }
        })

        dashbordViewModel.totalTimeInMillis.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeInMillis = TimeUtility.getTotalHoursFromMills(it)
                tvTotalTime.text = totalTimeInMillis
            }
        })

        dashbordViewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val roundedAvgSpeed = Math.round(it * 10f) / 10f
                val totalAvgSpeed = "${roundedAvgSpeed}"
                tvAverageSpeed.text = totalAvgSpeed
            }
        })

        dashbordViewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalCaloriesBurned = "${it}"
                tvTotalCalories.text = totalCaloriesBurned
            }
        })
    }

    fun initGoogleAdds(){
        try{
            with(view){
                if(addContainer.childCount>0){
                    addContainer.removeAllViews()
                }
                addContainer.addView(GoogleAddsUtil.getBannerAdd(requireContext()))
                println("add added===>")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}