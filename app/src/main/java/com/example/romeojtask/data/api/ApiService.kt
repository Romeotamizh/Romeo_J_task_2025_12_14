package com.example.romeojtask.data.api

import com.example.romeojtask.data.model.ApiResponse
import retrofit2.http.GET

interface ApiService {
    @GET(".")
    suspend fun getHoldings(): ApiResponse
}