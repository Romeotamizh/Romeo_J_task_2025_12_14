package com.example.romeojtask.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.romeojtask.data.api.RetrofitInstance
import com.example.romeojtask.data.db.AppDatabase
import com.example.romeojtask.data.db.HoldingEntity
import com.example.romeojtask.data.repository.HoldingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class PositionsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HoldingsRepository
    val holdingsStream: Flow<PagingData<HoldingEntity>>
    val allHoldingsForSummary: LiveData<List<HoldingEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = HoldingsRepository(database, RetrofitInstance.api)
        holdingsStream = repository.getHoldingsStream().cachedIn(viewModelScope)
        allHoldingsForSummary = database.holdingsDao().getAllHoldingsList()

        viewModelScope.launch {
            mutableListOf(1, 2, 3).asFlow()
                .buffer()
                .collect {
                println(it)
            }
        }
    }
}
