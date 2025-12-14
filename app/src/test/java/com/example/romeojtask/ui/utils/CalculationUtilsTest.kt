package com.example.romeojtask.ui.utils

import com.example.romeojtask.data.db.HoldingDetails
import com.example.romeojtask.data.db.HoldingEntity
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CalculationUtilsTest {

    @Test
    fun `calculateTodayPnl returns correct positive value`() {
        val holding = HoldingEntity(
            symbol = "",
            details = HoldingDetails(quantity = 10, ltp = 110.0, avgPrice = 0.0, close = 100.0)
        )
        val result = holding.calculateTodayPnl()
        assertThat(result).isEqualTo(100.0)
    }

    @Test
    fun `calculateTodayPnl returns correct negative value`() {
        val holding = HoldingEntity(
            symbol = "",
            details = HoldingDetails(quantity = 5, ltp = 95.0, avgPrice = 0.0, close = 100.0)
        )
        val result = holding.calculateTodayPnl()
        assertThat(result).isEqualTo(-25.0)
    }

    @Test
    fun `calculateTotalCurrentValue returns correct sum`() {
        val holdings = listOf(
            HoldingEntity("", HoldingDetails(quantity = 10, ltp = 110.0, avgPrice = 0.0, close = 0.0)), // 1100
            HoldingEntity("", HoldingDetails(quantity = 5, ltp = 200.0, avgPrice = 0.0, close = 0.0))  // 1000
        )
        val result = holdings.calculateTotalCurrentValue()
        assertThat(result).isEqualTo(2100.0)
    }

    @Test
    fun `calculateTotalInvestment returns correct sum`() {
        val holdings = listOf(
            HoldingEntity("", HoldingDetails(quantity = 10, ltp = 0.0, avgPrice = 100.0, close = 0.0)), // 1000
            HoldingEntity("", HoldingDetails(quantity = 5, ltp = 0.0, avgPrice = 180.0, close = 0.0))   // 900
        )
        val result = holdings.calculateTotalInvestment()
        assertThat(result).isEqualTo(1900.0)
    }

    @Test
    fun `calculateTodaysTotalPnl returns correct sum`() {
        val holdings = listOf(
            HoldingEntity("", HoldingDetails(quantity = 10, ltp = 110.0, avgPrice = 0.0, close = 100.0)), // +100
            HoldingEntity("", HoldingDetails(quantity = 5, ltp = 95.0, avgPrice = 0.0, close = 100.0))    // -25
        )
        val result = holdings.calculateTodayTotalPnl()
        assertThat(result).isEqualTo(75.0)
    }

    @Test
    fun `calculateOverallPnl returns correct difference`() {
        val result = calculateOverallPnl(2500.0, 2000.0)
        assertThat(result).isEqualTo(500.0)
    }

    @Test
    fun `calculateOverallPnlPercentage returns correct percentage`() {
        val result = calculateOverallPnlPercentage(500.0, 2000.0)
        assertThat(result).isEqualTo(25.0)
    }

    @Test
    fun `calculateOverallPnlPercentage returns 0 when investment is zero`() {
        val result = calculateOverallPnlPercentage(500.0, 0.0)
        assertThat(result).isEqualTo(0.0)
    }
}