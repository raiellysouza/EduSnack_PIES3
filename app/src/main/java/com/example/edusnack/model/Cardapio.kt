package com.example.edusnack.model

import java.util.*

data class Cardapio(
    val id: String = "",
    val data: Date = Date(),
    val refeicao: String = "",
    val descricao: String = "",
    val valorNutricional: String = "",
    val autorId: String = ""
)
