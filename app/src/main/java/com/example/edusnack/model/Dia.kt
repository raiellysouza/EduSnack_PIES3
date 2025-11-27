package com.example.edusnack.model

data class Dia(
    val data: String = "", // formato e "2025-11-25"
    val itens: MutableList<ItemPedido> = mutableListOf(),
    var totalDia: Double = 0.0,
    val nomeDia: String
)
