package com.example.romeojtask.data.model

import com.squareup.moshi.Json

data class ApiResponse(
    val data: HoldingsData
)

data class HoldingsData(
    @property:Json(name = "userHolding") val userHoldings: List<Holding>
)

data class Holding(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
)
