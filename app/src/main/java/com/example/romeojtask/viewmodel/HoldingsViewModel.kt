package com.example.romeojtask.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.romeojtask.data.db.AppDatabase
import com.example.romeojtask.data.db.HoldingEntity
import com.example.romeojtask.data.repository.HoldingsRepository
import kotlinx.coroutines.launch

class HoldingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HoldingsRepository
    val allHoldings: LiveData<List<HoldingEntity>>

    init {
        val holdingsDao = AppDatabase.getDatabase(application).holdingsDao()
        repository = HoldingsRepository(holdingsDao)
        allHoldings = repository.allHoldings
    }

    fun refreshHoldings() {
        viewModelScope.launch {
            repository.refreshHoldings()
        }
    }
}