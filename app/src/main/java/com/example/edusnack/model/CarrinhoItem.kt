package com.example.edusnack.model

data class CarrinhoItem(
    val item: Cardapio,
    var quantidade: Int = 1
) {
    fun subtotal(): Double = item.preco * quantidade
}