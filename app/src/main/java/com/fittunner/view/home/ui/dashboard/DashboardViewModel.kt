package com.fittunner.view.home.ui.dashboard

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fittunner.repository.RunRepository

class DashboardViewModel @ViewModelInject constructor(
    val repository: RunRepository
): ViewModel() {
    var totalDistance = repository.getTotalDistance()
    var totalTimeInMillis = repository.getTotalTimeInMillis()
    var totalAvgSpeed = repository.getTotalAvgSpeed()
    var totalCaloriesBurned = repository.getTotalCaloriesBurned()
}