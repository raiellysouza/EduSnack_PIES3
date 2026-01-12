package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.model.CarrinhoItem
import com.example.edusnack.model.Cardapio
import com.example.edusnack.model.ItemPedido
import com.example.edusnack.model.Pedido
import com.example.edusnack.model.StatusPedido
import com.example.edusnack.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class CarrinhoViewModel(
    private val pedidoRepo: PedidoRepository = PedidoRepository()
) : ViewModel() {

    private val _itens = MutableStateFlow<List<CarrinhoItem>>(emptyList())
    val itens = _itens.asStateFlow()

    fun adicionar(item: Cardapio) {
        val atual = _itens.value
        val idx = atual.indexOfFirst { it.item.id == item.id }

        _itens.value = if (idx == -1) {
            atual + CarrinhoItem(item, quantidade = 1)
        } else {
            atual.mapIndexed { i, ci ->
                if (i == idx) ci.copy(quantidade = ci.quantidade + 1) else ci
            }
        }
    }

    fun remover(item: Cardapio) {
        val atual = _itens.value
        val idx = atual.indexOfFirst { it.item.id == item.id }
        if (idx == -1) return

        val ci = atual[idx]
        _itens.value = if (ci.quantidade <= 1) {
            atual.filterIndexed { i, _ -> i != idx }
        } else {
            atual.mapIndexed { i, x ->
                if (i == idx) x.copy(quantidade = x.quantidade - 1) else x
            }
        }
    }


    fun total(): Double = _itens.value.sumOf { it.subtotal() }

    fun limpar() { _itens.value = emptyList() }

    fun finalizarCompra(usuarioId: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                if (usuarioId.isBlank()) {
                    onDone(null)
                    return@launch
                }

                val itensPedido = _itens.value.map { ci ->
                    ItemPedido(
                        itemId = ci.item.id,
                        nome = ci.item.nome,
                        preco = ci.item.preco, // Cardapio.preco é Double
                        quantidade = ci.quantidade,
                        preparoNaHora = false
                    )
                }
                val total = itensPedido.sumOf { it.preco?.times(it.quantidade) ?: 0.0 }

                val pedido = Pedido(
                    alunoId = usuarioId,
                    alunoNome = "",  // depois a gente puxa do profile
                    turma = "",      // depois a gente puxa do profile
                    itens = itensPedido,
                    status = StatusPedido.PENDENTE,
                    total = total,
                    codigoRetirada = gerarCodigoRetirada()
                )

                val res = pedidoRepo.salvarPedido(pedido)
                val id = res.getOrNull()

                if (id != null) limpar()

                onDone(id)
            } catch (e: Exception) {
                onDone(null)
            }
        }
    }

    private fun gerarCodigoRetirada(): String {
        // 6 dígitos
        return (100000..999999).random(Random(System.currentTimeMillis())).toString()
    }

}
