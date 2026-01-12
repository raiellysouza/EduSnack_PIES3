package com.example.edusnack.model

data class CarrinhoItem(
    val item: Cardapio,
    val quantidade: Int = 1
) {
    fun subtotal(): Double = item.preco?.times(quantidade) ?: 0.0
}