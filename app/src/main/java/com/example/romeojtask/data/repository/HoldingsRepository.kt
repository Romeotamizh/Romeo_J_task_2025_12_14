package com.example.romeojtask.data.repository

import androidx.lifecycle.LiveData
import com.example.romeojtask.data.api.RetrofitInstance
import com.example.romeojtask.data.db.HoldingsDao
import com.example.romeojtask.data.db.HoldingEntity
import com.example.romeojtask.data.model.Holding

class HoldingsRepository(private val holdingsDao: HoldingsDao) {

    val allHoldings: LiveData<List<HoldingEntity>> = holdingsDao.getAllHoldings()

    suspend fun refreshHoldings() {
        try {
            val response = RetrofitInstance.api.getHoldings()
            val holdingEntities = response.data.holdings.map { it.toEntity() }
            holdingsDao.upsertAll(holdingEntities)
        } catch (e: Exception) {
            // Handle error
        }
    }

    private fun Holding.toEntity() = HoldingEntity(
        symbol = symbol,
        quantity = quantity,
        ltp = ltp,
        close = close
    )
}