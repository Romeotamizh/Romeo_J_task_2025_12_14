package com.example.romeojtask

import retrofit2.http.GET

interface ApiService {
    @GET(".")
    suspend fun getHoldings(): ApiResponse
}