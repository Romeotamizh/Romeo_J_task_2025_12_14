package com.example.romeojtask.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import com.example.romeojtask.data.db.AppDatabase
import com.example.romeojtask.data.db.HoldingDetails
import com.example.romeojtask.data.db.HoldingEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@ExperimentalPagingApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class HoldingsRepositoryTest {

    private lateinit var inMemoryDb: AppDatabase

    @Before
    fun setUp() {
        inMemoryDb = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        inMemoryDb.close()
    }

    @Test
    fun `pager returns data from database`() = runBlocking {
        // Arrange: Insert data directly into the database
        val testData = listOf(
            HoldingEntity("TEST1", HoldingDetails(10, 100.0, 90.0, 95.0)),
            HoldingEntity("TEST2", HoldingDetails(20, 200.0, 190.0, 195.0))
        )
        inMemoryDb.holdingsDao().upsertAll(testData)

        val pager = Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { inMemoryDb.holdingsDao().getAllHoldings() }
        )

        val snapshot = pager.flow.asSnapshot()

        assertThat(snapshot).hasSize(2)
        assertThat(snapshot.first().symbol).isEqualTo("TEST1")
    }
}