package com.example.edusnack.model

data class ItemPedido(
    val itemId: String = "",
    val nome: String = "",
    val preco: Double? = 0.0,
    val quantidade: Int = 1,
    val preparoNaHora: Boolean = false
)
