package com.fittuner.view.home.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fittuner.repository.SessionRepository

class SettingsViewModel @ViewModelInject constructor(
    val sessionRepository: SessionRepository
): ViewModel() {

}