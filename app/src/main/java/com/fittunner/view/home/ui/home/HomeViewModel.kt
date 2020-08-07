package com.fittunner.view.home.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fittunner.repository.RunRepository



public class HomeViewModel @ViewModelInject constructor(
    val repository: RunRepository
) : ViewModel() {

   /* private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text*/

    fun getAllRuns()=repository.getAllRunsBySortByDate()
}