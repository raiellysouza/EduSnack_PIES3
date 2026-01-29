package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.model.CarrinhoItem
import com.example.edusnack.model.Cardapio
import com.example.edusnack.model.ItemPedido
import com.example.edusnack.model.Pedido
import com.example.edusnack.model.StatusPedido
import com.example.edusnack.repository.PedidoRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class CarrinhoViewModel(
    private val pedidoRepo: PedidoRepository = PedidoRepository(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _itens = MutableStateFlow<List<CarrinhoItem>>(emptyList())
    val itens = _itens.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun adicionar(item: Cardapio, dias: List<String> = emptyList()) {
        val atual = _itens.value
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
    fun clearError() { _error.value = null }

    fun finalizarCompra(usuarioId: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                if (usuarioId.isBlank()) {
                    _error.value = "Usuário não identificado"
                    onDone(null)
                    return@launch
                }

                val totalPedido = total()

                // 1. Verificar saldo atualizado no Firestore (coleção "usuarios")
                val usuarioDoc = db.collection("usuarios").document(usuarioId).get().await()
                if (!usuarioDoc.exists()) {
                     _error.value = "Perfil de usuário não encontrado"
                    onDone(null)
                    return@launch
                }

                val saldoAtual = usuarioDoc.getDouble("saldo") ?: 0.0

                if (saldoAtual < totalPedido) {
                    _error.value = "Saldo insuficiente! Você tem R$ ${String.format("%.2f", saldoAtual)}"
                    onDone(null)
                    return@launch
                }

                // 2. Prosseguir com a criação do pedido
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

                val pedido = Pedido(
                    alunoId = usuarioId,
                    alunoNome = "", 
                    turma = "",
                    itens = itensPedido,
                    status = StatusPedido.PENDENTE,
                    total = totalPedido,
                    codigoRetirada = gerarCodigoRetirada()
                )

                val res = pedidoRepo.salvarPedido(pedido)
                val ultimoId = res.getOrNull()

                if (ultimoId != null) {
                    limpar()
                    onDone(ultimoId)
                } else {
                    _error.value = "Erro ao salvar pedido"
                    onDone(null)
                }

            } catch (e: Exception) {
                _error.value = "Erro: ${e.message}"
                onDone(null)
            }
        }
    }

    private fun gerarCodigoRetirada(): String {
        return (100000..999999).random(Random(System.currentTimeMillis())).toString()
    }

}
