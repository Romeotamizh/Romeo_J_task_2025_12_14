package com.example.romeojtask.data.model

import com.squareup.moshi.Json

data class ApiResponse(
    val data: HoldingsData
)

data class HoldingsData(
    @Json(name = "userHolding") val holdings: List<Holding>
)

data class Holding(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val close: Double
)
