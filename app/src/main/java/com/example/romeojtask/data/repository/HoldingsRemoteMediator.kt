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
            // For this example, we only ever load the first page from the mock API.
            // A real paginated API would use a page key from the `state`.
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                // We've reached the end of the list, so we don't need to load more.
                LoadType.PREPEND, LoadType.APPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            }

            // API Call - In a real app, you would pass the 'page' number to the API.
            val response = apiService.getHoldings()
            val holdings = response.data.holdings

            // Save to Database inside a transaction
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    holdingsDao.clearAllHoldings()
                }
                val entities = holdings.map { it.toEntity() }
                holdingsDao.upsertAll(entities)
            }

            // Since the mock API returns everything at once, we've reached the end.
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
        close = close
    )
}