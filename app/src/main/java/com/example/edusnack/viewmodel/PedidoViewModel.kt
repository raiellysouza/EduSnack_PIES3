package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.model.Pedido
import com.example.edusnack.model.StatusPedido
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
                db.collection("pedidos")
                    .document(orderId)
                    .update(
                        mapOf(
                            "status" to StatusPedido.ENTREGUE.name,
                            "entregueEm" to FieldValue.serverTimestamp()
                        )
                    )
                    .await()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            try {
                db.collection("pedidos")
                    .document(orderId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
