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

    // Atualizado para aceitar os dias de reserva
    fun adicionar(item: Cardapio, dias: List<String> = emptyList()) {
        val atual = _itens.value
        // Se já existe o item com os MESMOS dias, aumenta a quantidade. 
        // Se os dias forem diferentes, tratamos como um novo item no carrinho para não confundir.
        val idx = atual.indexOfFirst { it.item.id == item.id && it.diasReserva == dias }

        _itens.value = if (idx == -1) {
            atual + CarrinhoItem(item, quantidade = 1, diasReserva = dias)
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
                        preco = ci.item.preco,
                        quantidade = ci.quantidade,
                        preparoNaHora = false,
                        diasReserva = ci.diasReserva // PASSANDO OS DIAS PARA O PEDIDO REAL
                    )
                }
                
                // Cálculo do total considerando a quantidade de dias
                val total = _itens.value.sumOf { it.subtotal() }

                val pedido = Pedido(
                    alunoId = usuarioId,
                    alunoNome = "", 
                    turma = "",      
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
        return (100000..999999).random(Random(System.currentTimeMillis())).toString()
    }
}
