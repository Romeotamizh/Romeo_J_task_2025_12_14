package com.example.romeojtask.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.romeojtask.data.api.ApiService
import com.example.romeojtask.data.db.AppDatabase
import com.example.romeojtask.data.db.HoldingEntity
import com.example.romeojtask.data.model.Holding
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class HoldingsRemoteMediator(
    private val database: AppDatabase,
    private val apiService: ApiService
) : RemoteMediator<Int, HoldingEntity>() {

    private val holdingsDao = database.holdingsDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, HoldingEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND, LoadType.APPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            }

            val response = apiService.getHoldings()
            val holdings = response.data.holdings

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    holdingsDao.clearAllHoldings()
                }
                val entities = holdings.map { it.toEntity() }
                holdingsDao.upsertAll(entities)
            }

            MediatorResult.Success(endOfPaginationReached = true)

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private fun Holding.toEntity() = HoldingEntity(
        symbol = symbol,
        quantity = quantity,
        ltp = ltp,
        avgPrice = avgPrice, // This line was missing
        close = close
    )
}