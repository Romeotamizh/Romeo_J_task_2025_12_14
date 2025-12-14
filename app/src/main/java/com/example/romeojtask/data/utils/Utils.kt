package com.example.romeojtask.data.utils

import com.example.romeojtask.data.db.HoldingEntity

object CalculationUtils {

    fun calculateTodaysPnl(ltp: Double, close: Double, quantity: Int): Double {
        return (ltp - close) * quantity
    }

    fun calculateTotalCurrentValue(holdings: List<HoldingEntity>): Double {
        return holdings.sumOf { it.ltp * it.quantity }
    }

    fun calculateTotalInvestment(holdings: List<HoldingEntity>): Double {
        return holdings.sumOf { it.avgPrice * it.quantity }
    }

    fun calculateTodaysTotalPnl(holdings: List<HoldingEntity>): Double {
        return holdings.sumOf { (it.ltp - it.close) * it.quantity }
    }

    fun calculateOverallPnl(totalCurrentValue: Double, totalInvestment: Double): Double {
        return totalCurrentValue - totalInvestment
    }

    fun calculateOverallPnlPercentage(overallPnl: Double, totalInvestment: Double): Double {
        return if (totalInvestment > 0) (overallPnl / totalInvestment) * 100 else 0.0
    }
}