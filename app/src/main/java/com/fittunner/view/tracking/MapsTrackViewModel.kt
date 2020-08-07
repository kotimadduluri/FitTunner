package com.fittunner.view.tracking

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittunner.repository.RunRepository
import com.fittunner.room.RunTrack
import kotlinx.coroutines.launch

class MapsTrackViewModel @ViewModelInject constructor(
    val repository: RunRepository
) : ViewModel(){

    fun insertRun(run:RunTrack)=viewModelScope.launch {
        repository.insertRun(run)
    }
}