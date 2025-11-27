package com.example.edusnack.model

data class ResumoSemanal(
    val idSemana: String = "",
    val alunoId: String = "",
    val dias: List<DiaResumo> = emptyList(),
    val totalGeral: Double = 0.0,
    val quantidadeDias: Int = 0,
    val quantidadeItensTotal: Int = 0
)



