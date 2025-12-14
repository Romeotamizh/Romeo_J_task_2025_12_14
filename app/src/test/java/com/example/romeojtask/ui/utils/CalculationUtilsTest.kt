package com.example.romeojtask.ui.utils

import com.example.romeojtask.data.db.HoldingEntity
import com.google.common.truth.Truth
import org.junit.Test

class CalculationUtilsTest {

    @Test
    fun `calculateTodayPnl returns correct positive value`() {
        val result = HoldingEntity("", 10, 110.0, 0.0, 100.0).calculateTodayPnl()
        Truth.assertThat(result).isEqualTo(100.0)
    }

    @Test
    fun `calculateTodayPnl returns correct negative value`() {
        val result = HoldingEntity("", 5, 95.0, 0.0, 100.0).calculateTodayPnl()
        Truth.assertThat(result).isEqualTo(-25.0)
    }

    @Test
    fun `calculateTotalCurrentValue returns correct sum`() {
        val holdings = listOf(
            HoldingEntity("", 10, 110.0, 0.0, 0.0), // 1100
            HoldingEntity("", 5, 200.0, 0.0, 0.0)  // 1000
        )
        val result = holdings.calculateTotalCurrentValue()
        Truth.assertThat(result).isEqualTo(2100.0)
    }

    @Test
    fun `calculateTotalInvestment returns correct sum`() {
        val holdings = listOf(
            HoldingEntity("", 10, 0.0, 100.0, 0.0), // 1000
            HoldingEntity("", 5, 0.0, 180.0, 0.0)   // 900
        )
        val result = holdings.calculateTotalInvestment()
        Truth.assertThat(result).isEqualTo(1900.0)
    }

    @Test
    fun `calculateTodayTotalPnl returns correct sum`() {
        val holdings = listOf(
            HoldingEntity("", 10, 110.0, 0.0, 100.0), // +100
            HoldingEntity("", 5, 95.0, 0.0, 100.0)    // -25
        )
        val result = holdings.calculateTodayTotalPnl()
        Truth.assertThat(result).isEqualTo(75.0)
    }

    @Test
    fun `calculateOverallPnl returns correct difference`() {
        val result = calculateOverallPnl(2500.0, 2000.0)
        Truth.assertThat(result).isEqualTo(500.0)
    }

    @Test
    fun `calculateOverallPnlPercentage returns correct percentage`() {
        val result = calculateOverallPnlPercentage(500.0, 2000.0)
        Truth.assertThat(result).isEqualTo(25.0)
    }

    @Test
    fun `calculateOverallPnlPercentage returns 0 when investment is zero`() {
        val result = calculateOverallPnlPercentage(500.0, 0.0)
        Truth.assertThat(result).isEqualTo(0.0)
    }
}