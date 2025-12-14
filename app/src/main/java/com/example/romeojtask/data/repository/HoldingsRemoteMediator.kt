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
            when (loadType) {
                LoadType.REFRESH -> {  } /* Since this is not a paginated api, we'll just load the whole data */
                // REACHED EOL!!!
                else -> return MediatorResult.Success(endOfPaginationReached = true)
            }

            val response = apiService.getHoldings()
            val holdings = response.data.holdings

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    holdingsDao.clearAllHoldings()
                }
                holdings.map { it.toEntity() }.let {
                    holdingsDao.upsertAll(it)
                }
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
        avgPrice = avgPrice,
        close = close
    )
}