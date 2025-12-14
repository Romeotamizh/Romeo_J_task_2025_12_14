package com.example.romeojtask.ui.utils

import com.example.romeojtask.data.db.HoldingEntity

fun HoldingEntity.calculateTodayPnl(): Double {
    return (this.ltp - this.close) * this.quantity
}

fun List<HoldingEntity>.calculateTotalCurrentValue(): Double {
    return this.sumOf { it.ltp * it.quantity }
}

fun List<HoldingEntity>.calculateTotalInvestment(): Double {
    return this.sumOf { it.avgPrice * it.quantity }
}

fun List<HoldingEntity>.calculateTodayTotalPnl(): Double {
    return this.sumOf { it.calculateTodayPnl() }
}

fun calculateOverallPnl(totalCurrentValue: Double, totalInvestment: Double): Double {
    return totalCurrentValue - totalInvestment
}

fun calculateOverallPnlPercentage(overallPnl: Double, totalInvestment: Double): Double {
    return if (totalInvestment > 0) (overallPnl / totalInvestment) * 100 else 0.0
}
