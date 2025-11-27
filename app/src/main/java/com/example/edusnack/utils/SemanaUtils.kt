package com.example.edusnack.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDate

object SemanaUtils {


    @RequiresApi(Build.VERSION_CODES.O)
    fun calcularIdSemana(data: LocalDate): String {
        val monday = data.with(DayOfWeek.MONDAY)
        return monday.toString() // formato "YYYY-MM-DD"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun gerarSemanaCompleta(data: LocalDate): List<String> {
        val segunda = data.with(DayOfWeek.MONDAY)
        return (0..4).map { segunda.plusDays(it.toLong()).toString() }
    }
}
