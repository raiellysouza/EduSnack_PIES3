package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.model.Pedido
import com.example.edusnack.model.StatusPedido
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PedidoViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _ordersBySeries = MutableStateFlow<Map<String, List<Pedido>>>(emptyMap())
    val ordersBySeries: StateFlow<Map<String, List<Pedido>>> = _ordersBySeries

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val seriesList = buildList {
        for (i in 1..9) add("${i}º Fundamental")
        add("1º Ensino Médio")
        add("2º Ensino Médio")
        add("3º Ensino Médio")
        add("Outros")
    }

    init {
        subscribeToOrders()
    }

    private fun normalizeSeries(turma: String?): String {
        val t = (turma ?: "").trim()

        if (t.contains("ensino médio", ignoreCase = true) || t.contains("EM", ignoreCase = true)) {
            return when {
                t.contains("1") -> "1º Ensino Médio"
                t.contains("2") -> "2º Ensino Médio"
                t.contains("3") -> "3º Ensino Médio"
                else -> "1º Ensino Médio"
            }
        }

        val num = t.takeWhile { it.isDigit() }.toIntOrNull()
        if (num != null && num in 1..9) return "${num}º Fundamental"

        return "Outros"
    }

    private fun subscribeToOrders() {
        _loading.value = true
        db.collection("pedidos").addSnapshotListener { snap, ex ->
            if (ex != null) {
                _error.value = ex.message
                _loading.value = false
                return@addSnapshotListener
            }

            val pedidos = snap?.documents?.mapNotNull { doc ->
                try { doc.toObject(Pedido::class.java) } catch (_: Exception) { null }
            } ?: emptyList()

            val grouped = seriesList.associateWith { series ->
                pedidos.filter { pedido -> normalizeSeries(pedido.turma) == series }
            }

            _ordersBySeries.value = grouped
            _loading.value = false
        }
    }

    fun updateStatus(orderId: String, status: StatusPedido) {
        viewModelScope.launch {
            try {
                db.collection("pedidos")
                    .document(orderId)
                    .update("status", status.name)
                    .await()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun markAsDelivered(orderId: String) {
        viewModelScope.launch {
            try {
                // 1. Buscar dados do pedido para saber quem é o aluno e qual o valor
                val orderSnap = db.collection("pedidos").document(orderId).get().await()
                val pedido = orderSnap.toObject(Pedido::class.java) ?: return@launch
                val alunoId = pedido.alunoId
                val totalPedido = pedido.total

                // 2. Executar transação para garantir que o saldo seja descontado apenas se a entrega for confirmada
                db.runTransaction { transaction ->
                    val alunoRef = db.collection("usuarios").document(alunoId)
                    val alunoSnap = transaction.get(alunoRef)
                    
                    val saldoAtual = alunoSnap.getDouble("saldo") ?: 0.0
                    val novoSaldo = saldoAtual - totalPedido

                    // Atualiza saldo do aluno
                    transaction.update(alunoRef, "saldo", novoSaldo)
                    
                    // Atualiza status do pedido
                    val pedidoRef = db.collection("pedidos").document(orderId)
                    transaction.update(pedidoRef, mapOf(
                        "status" to StatusPedido.ENTREGUE.name,
                        "entregueEm" to FieldValue.serverTimestamp()
                    ))
                }.await()

            } catch (e: Exception) {
                _error.value = "Erro ao processar entrega: ${e.message}"
            }
        }
    }

    fun cancelOrder(pedidoId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                db.collection("pedidos")
                    .document(pedidoId)
                    .update(
                        mapOf(
                            "status" to StatusPedido.CANCELADO.name,
                            "canceladoEm" to FieldValue.serverTimestamp(),
                            "canceladoPor" to "CANTINEIRO"
                        )
                    )
                    .await()

            } catch (e: Exception) {
                _error.value = "Erro ao cancelar pedido: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
