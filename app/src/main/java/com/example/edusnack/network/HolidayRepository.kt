package com.example.edusnack.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HolidayRepository {
    private val api = Retrofit.Builder()
        .baseUrl("https://brasilapi.com.br/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BrasilApiService::class.java)

    private var cachedHolidays = mutableMapOf<Int, List<Holiday>>()

    private suspend fun getHolidaysForYear(year: Int): List<Holiday> {
        return if (cachedHolidays.containsKey(year)) {
            cachedHolidays[year]!!
        } else {
            val fetched = api.getHolidays(year)
            cachedHolidays[year] = fetched
            fetched
        }
    }

    suspend fun isTodayHoliday(): Boolean {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(calendar.time)

        return try {
            val holidays = getHolidaysForYear(year)
            holidays.any { it.date == todayStr }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getNextHoliday(): Holiday? {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(calendar.time)

        return try {
            val holidays = getHolidaysForYear(year)
            // Filtra feriados que são após hoje e pega o primeiro
            val next = holidays.filter { it.date > todayStr }.minByOrNull { it.date }
            
            // Se não houver mais feriados este ano, busca no próximo
            next ?: getHolidaysForYear(year + 1).minByOrNull { it.date }
        } catch (e: Exception) {
            null
        }
    }
}
