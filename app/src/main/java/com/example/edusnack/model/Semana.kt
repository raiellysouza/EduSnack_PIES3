package com.example.edusnack.model

data class Semana(
    val idSemana: String = "",   // ex: "2025-S01"
    val alunoId: String = "",
    val dias: MutableList<Dia> = mutableListOf()
)
