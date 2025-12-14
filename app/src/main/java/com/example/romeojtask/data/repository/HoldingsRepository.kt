package com.example.romeojtask.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.romeojtask.data.api.ApiService
import com.example.romeojtask.data.db.AppDatabase
import com.example.romeojtask.data.db.HoldingEntity
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class HoldingsRepository(private val database: AppDatabase, private val apiService: ApiService) {

    val allHoldingsForSummary: LiveData<List<HoldingEntity>> = database.holdingsDao().getAllHoldingsList()

    fun getHoldingsStream(): Flow<PagingData<HoldingEntity>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        remoteMediator = HoldingsRemoteMediator(
            database = database,
            apiService = apiService
        ),
        pagingSourceFactory = { database.holdingsDao().getAllHoldings() }
    ).flow
}