package com.example.edusnack.network

import retrofit2.http.GET
import retrofit2.http.Path

data class Holiday(
    val date: String,
    val name: String,
    val type: String
)

interface BrasilApiService {
    @GET("feriados/v1/{year}")
    suspend fun getHolidays(@Path("year") year: Int): List<Holiday>
}
