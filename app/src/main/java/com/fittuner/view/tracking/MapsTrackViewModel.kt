package com.fittuner.view.tracking

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittuner.repository.RunRepository
import com.fittuner.room.RunTrack
import kotlinx.coroutines.launch

class MapsTrackViewModel @ViewModelInject constructor(
    val repository: RunRepository
) : ViewModel(){

    fun insertRun(run:RunTrack)=viewModelScope.launch {
        repository.insertRun(run)
    }
}