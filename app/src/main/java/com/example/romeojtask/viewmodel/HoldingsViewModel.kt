package com.example.romeojtask.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.romeojtask.data.db.AppDatabase
import com.example.romeojtask.data.db.HoldingEntity
import com.example.romeojtask.data.repository.HoldingsRepository
import kotlinx.coroutines.flow.Flow

class HoldingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HoldingsRepository
    val holdingsStream: Flow<PagingData<HoldingEntity>>
    val allHoldingsForSummary: LiveData<List<HoldingEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = HoldingsRepository(database)
        holdingsStream = repository.getHoldingsStream().cachedIn(viewModelScope)
        allHoldingsForSummary = repository.allHoldingsForSummary
    }
}