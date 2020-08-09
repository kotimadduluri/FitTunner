package com.fittuner.view.home.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.fittuner.R
import com.fittuner.view.InitialSetupActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editProfile.setOnClickListener {
            requireContext().startActivity(Intent(
                requireContext(),
                InitialSetupActivity::class.java
            ))
        }
    }

    override fun onResume() {
        super.onResume()
        setProfileData()
    }

    private fun setProfileData() {
        userName.text=settingsViewModel.sessionRepository.getUserName()
        userWeight.text="${settingsViewModel.sessionRepository.getUserWeight()} kgs"
    }
}