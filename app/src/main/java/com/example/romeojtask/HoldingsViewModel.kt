package com.example.romeojtask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HoldingsViewModel : ViewModel() {

    private val _holdings = MutableLiveData<List<Holding>>()
    val holdings: LiveData<List<Holding>> = _holdings

    fun fetchHoldings() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getHoldings()
                _holdings.value = response.data.holdings
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}