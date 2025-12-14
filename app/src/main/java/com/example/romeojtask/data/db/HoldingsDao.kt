package com.example.romeojtask.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HoldingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(holdings: List<HoldingEntity>)

    @Query("SELECT * FROM holdings")
    fun getAllHoldings(): PagingSource<Int, HoldingEntity>

    @Query("DELETE FROM holdings")
    suspend fun clearAllHoldings()
}