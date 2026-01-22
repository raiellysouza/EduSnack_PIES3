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

    // Flag para controlar operação de salvamento e permitir feedback UI
    private val _saving = MutableStateFlow(false)
    val saving = _saving.asStateFlow()

    fun adicionar(item: Cardapio, dias: List<String> = emptyList()) {
        val atual = _itens.value
        val idx = atual.indexOfFirst { it.item.id == item.id && it.diasReserva == dias }

        _itens.value = if (idx == -1) {
            atual + CarrinhoItem(item, quantidade = 1, diasReserva = dias)
        } else {
            _itens.value.mapIndexed { i, ci ->
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
                // evita dupla submissão
                if (_saving.value) {
                    onDone(null)
                    return@launch
                }

                if (usuarioId.isBlank()) {
                    onDone(null)
                    return@launch
                }

                if (_itens.value.isEmpty()) {
                    onDone(null)
                    return@launch
                }

                _saving.value = true

                // Construir um único pedido agregando todos os itens do carrinho
                val itensPedido = _itens.value.map { ci ->
                    ItemPedido(
                        itemId = ci.item.id,
                        nome = ci.item.nome,
                        preco = ci.item.preco,
                        quantidade = ci.quantidade,
                        preparoNaHora = false,
                        diasReserva = ci.diasReserva
                    )
                }

                // Calcular total considerando diasReserva (cada dia conta como uma unidade)
                val totalPedido = _itens.value.sumOf { ci ->
                    val unidades = if (ci.diasReserva.isNotEmpty()) ci.diasReserva.size else ci.quantidade
                    (ci.item.preco ?: 0.0) * unidades
                }

                val pedido = Pedido(
                    alunoId = usuarioId,
                    alunoNome = "", // Preenchido pelo repositório se necessário
                    turma = "",
                    itens = itensPedido,
                    status = StatusPedido.PENDENTE,
                    total = totalPedido,
                    codigoRetirada = gerarCodigoRetirada()
                )

                val res = pedidoRepo.salvarPedido(pedido)
                val ultimoId = res.getOrNull()

                limpar()
                onDone(ultimoId)

            } catch (e: Exception) {
                onDone(null)
            } finally {
                _saving.value = false
            }
        }
    }

    private fun gerarCodigoRetirada(): String {
        return (100000..999999).random(Random(System.currentTimeMillis())).toString()
    }

}
