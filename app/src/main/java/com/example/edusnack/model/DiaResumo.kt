package com.example.edusnack.model

data class DiaResumo(
    val data: String = "",
    val itens: List<ItemPedido> = emptyList(),
    val totalDia: Double = 0.0,
    val quantidadeItens: Int = 0
)
