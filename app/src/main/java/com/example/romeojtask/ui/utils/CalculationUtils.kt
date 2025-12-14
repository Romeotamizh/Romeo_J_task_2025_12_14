package com.example.romeojtask.ui.utils

import com.example.romeojtask.data.db.HoldingEntity

fun HoldingEntity.calculateTodayPnl(): Double {
    return (this.details.ltp - this.details.close) * this.details.quantity
}

fun List<HoldingEntity>.calculateTotalCurrentValue(): Double {
    return this.sumOf { it.details.ltp * it.details.quantity }
}

fun List<HoldingEntity>.calculateTotalInvestment(): Double {
    return this.sumOf { it.details.avgPrice * it.details.quantity }
}

fun List<HoldingEntity>.calculateTodaysTotalPnl(): Double {
    return this.sumOf { it.calculateTodayPnl() }
}

fun calculateOverallPnl(totalCurrentValue: Double, totalInvestment: Double): Double {
    return totalCurrentValue - totalInvestment
}

fun calculateOverallPnlPercentage(overallPnl: Double, totalInvestment: Double): Double {
    return if (totalInvestment > 0) (overallPnl / totalInvestment) * 100 else 0.0
}
