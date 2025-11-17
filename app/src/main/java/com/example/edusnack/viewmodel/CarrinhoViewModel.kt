package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.model.CarrinhoItem
import com.example.edusnack.model.Cardapio
import com.example.edusnack.model.Pedido
import com.example.edusnack.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarrinhoViewModel(
    private val pedidoRepo: PedidoRepository = PedidoRepository()
) : ViewModel() {

    private val _itens = MutableStateFlow<List<CarrinhoItem>>(emptyList())
    val itens = _itens.asStateFlow()

    fun adicionar(item: Cardapio) {
        val copia = _itens.value.toMutableList()
        val existente = copia.find { it.item.id == item.id }
        if (existente != null) existente.quantidade++ else copia.add(CarrinhoItem(item))
        _itens.value = copia
    }

    fun remover(item: Cardapio) {
        val copia = _itens.value.toMutableList()
        val existente = copia.find { it.item.id == item.id }
        if (existente != null) {
            existente.quantidade--
            if (existente.quantidade <= 0) copia.remove(existente)
        }
        _itens.value = copia
    }

    fun total(): Double = _itens.value.sumOf { it.subtotal() }

    fun limparCarrinho() { _itens.value = emptyList() }

    fun finalizarCompra(usuarioId: String, callback: (String?) -> Unit) {
        viewModelScope.launch {
            val pedido = Pedido(alunoId = usuarioId)
            val id = pedidoRepo.salvarPedido(pedido)
            callback(id.toString())
            if (id != null) limparCarrinho()
        }
    }
}
