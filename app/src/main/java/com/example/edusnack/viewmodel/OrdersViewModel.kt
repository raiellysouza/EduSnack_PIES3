package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.model.Pedido
import com.example.edusnack.model.StatusPedido
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrdersViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _ordersBySeries = MutableStateFlow<Map<String, List<Pedido>>>(emptyMap())
    val ordersBySeries: StateFlow<Map<String, List<Pedido>>> = _ordersBySeries

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val seriesList = listOf(
        "6º Ano",
        "7º Ano",
        "8º Ano",
        "9º Ano",
        "1º Ensino Médio",
        "2º Ensino Médio",
        "3º Ensino Médio",
        "Outros"
    )

    init {
        subscribeToOrders()
    }

    private fun normalizeSeries(turma: String?): String {
        val t = turma ?: ""
        val num = t.takeWhile { it.isDigit() }.toIntOrNull()
        return when {
            num == 6 -> "6º Ano"
            num == 7 -> "7º Ano"
            num == 8 -> "8º Ano"
            num == 9 -> "9º Ano"
            num == 1 -> "1º Ensino Médio"
            num == 2 -> "2º Ensino Médio"
            num == 3 -> "3º Ensino Médio"
            t.contains("Ensino Médio", ignoreCase = true) || t.contains("EM", ignoreCase = true) -> {
                // attempt to detect which year
                when {
                    t.contains("1", ignoreCase = true) -> "1º Ensino Médio"
                    t.contains("2", ignoreCase = true) -> "2º Ensino Médio"
                    t.contains("3", ignoreCase = true) -> "3º Ensino Médio"
                    else -> "1º Ensino Médio"
                }
            }
            else -> "Outros"
        }
    }

    private fun subscribeToOrders() {
        _loading.value = true
        db.collection("pedidos").addSnapshotListener { snap, ex ->
            if (ex != null) {
                _error.value = ex.message
                _loading.value = false
                return@addSnapshotListener
            }

            if (snap == null) {
                _ordersBySeries.value = emptyMap()
                _loading.value = false
                return@addSnapshotListener
            }

            val pedidos = snap.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Pedido::class.java)
                } catch (e: Exception) {
                    null
                }
            }.filter { pedido ->
                // Only show orders that are not delivered
                pedido.status != StatusPedido.ENTREGUE
            }

            val grouped = seriesList.associateWith { series ->
                pedidos.filter { pedido -> normalizeSeries(pedido.turma) == series }
            }

            _ordersBySeries.value = grouped
            _loading.value = false
        }
    }

    fun markAsDelivered(orderId: String) {
        viewModelScope.launch {
            try {
                db.collection("pedidos").document(orderId).update("status", StatusPedido.ENTREGUE.name).await()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
