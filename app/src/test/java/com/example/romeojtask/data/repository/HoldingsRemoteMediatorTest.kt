package com.example.romeojtask.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import com.example.romeojtask.data.api.ApiService
import com.example.romeojtask.data.db.AppDatabase
import com.example.romeojtask.data.db.HoldingEntity
import com.example.romeojtask.data.model.ApiResponse
import com.example.romeojtask.data.model.Holding
import com.example.romeojtask.data.model.HoldingsData
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.IOException

@ExperimentalPagingApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class HoldingsRemoteMediatorTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val mockApiService: ApiService = mockk()
    private val inMemoryDb = Room.inMemoryDatabaseBuilder(
        RuntimeEnvironment.getApplication(),
        AppDatabase::class.java
    ).allowMainThreadQueries().build()

    private val mediator = HoldingsRemoteMediator(inMemoryDb, mockApiService)

    @After
    fun tearDown() {
        inMemoryDb.close()
    }

    @Test
    fun `refreshLoad returns success and updates db when api returns data`() = runBlocking {
        // Arrange
        val mockHoldings = listOf(Holding("TEST", 10, 100.0, 90.0, 95.0))
        val mockResponse = ApiResponse(HoldingsData(mockHoldings))
        coEvery { mockApiService.getHoldings() } returns mockResponse

        val pagingState = PagingState<Int, HoldingEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(20),
            leadingPlaceholderCount = 0
        )

        // Act
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // Assert
        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()

        val dbData = inMemoryDb.holdingsDao().getAllHoldingsList().getOrAwaitValue()
        assertThat(dbData).hasSize(1)
        assertThat(dbData.first().symbol).isEqualTo("TEST")
    }

    @Test
    fun `refreshLoad returns error when api throws an exception`() = runBlocking {
        // Arrange
        coEvery { mockApiService.getHoldings() } throws IOException("Network failed")

        val pagingState = PagingState<Int, HoldingEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(20),
            leadingPlaceholderCount = 0
        )

        // Act
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // Assert
        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Error::class.java)
    }

    // Helper to get LiveData value in a test
    private fun <T> LiveData<T>.getOrAwaitValue(): T {
        var data: T? = null
        val latch = java.util.concurrent.CountDownLatch(1)
        val observer = object : androidx.lifecycle.Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)
        latch.await(2, java.util.concurrent.TimeUnit.SECONDS)
        return data!!
    }
}