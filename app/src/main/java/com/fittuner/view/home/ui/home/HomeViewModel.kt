package com.fittuner.view.home.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fittuner.repository.RunRepository
import com.fittuner.room.RunTrack


class HomeViewModel @ViewModelInject constructor(
    val repository: RunRepository
) : ViewModel() {

    fun getAllRuns()=repository.getAllRunsBySortByDate()

    val concertList: LiveData<PagedList<RunTrack>> =
        LivePagedListBuilder(repository.getAllRuns(),20)
            .build()

}