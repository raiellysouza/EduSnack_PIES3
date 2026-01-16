package com.example.edusnack.model

data class CarrinhoItem(
    val item: Cardapio,
    val quantidade: Int = 1,
    val diasReserva: List<String> = emptyList() // Adicionado campo para os dias
) {
    fun subtotal(): Double = (item.preco ?: 0.0) * if (diasReserva.isNotEmpty()) diasReserva.size else quantidade
}
