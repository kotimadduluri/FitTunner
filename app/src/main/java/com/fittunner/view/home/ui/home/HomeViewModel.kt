package com.fittunner.view.home.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.fittunner.repository.RunRepository


class HomeViewModel @ViewModelInject constructor(
    val repository: RunRepository
) : ViewModel() {

    fun getAllRuns()=repository.getAllRunsBySortByDate()
}