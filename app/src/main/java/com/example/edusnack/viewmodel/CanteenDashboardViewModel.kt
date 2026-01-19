package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.model.Pedido
import com.example.edusnack.model.StatusPedido
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class CanteenDashboardViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _pendingOrdersCount = MutableStateFlow(0)
    val pendingOrdersCount: StateFlow<Int> = _pendingOrdersCount

    private val _readyOrdersCount = MutableStateFlow(0)
    val readyOrdersCount: StateFlow<Int> = _readyOrdersCount

    private val _totalSales = MutableStateFlow(0.0)
    val totalSales: StateFlow<Double> = _totalSales

    private val _activeMenusCount = MutableStateFlow(0)
    val activeMenusCount: StateFlow<Int> = _activeMenusCount

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _loading.value = true

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = Timestamp(calendar.time)

            // 1) Contadores de pedidos do dia (criados hoje)
            db.collection("pedidos")
                .whereGreaterThanOrEqualTo("data", startOfDay)
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.let {
                        val pedidos = it.toObjects(Pedido::class.java)
                        _pendingOrdersCount.value = pedidos.count { p -> p.status == StatusPedido.PENDENTE }
                        _readyOrdersCount.value = pedidos.count { p -> p.status == StatusPedido.PRONTO }
                    }
                }

            // 2) Total de vendas do dia (somente ENTREGUE hoje, baseado em entregueEm)
            db.collection("pedidos")
                .whereGreaterThanOrEqualTo("entregueEm", startOfDay)
                .whereEqualTo("status", StatusPedido.ENTREGUE.name)
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.let {
                        val total = it.documents.sumOf { doc -> doc.getDouble("total") ?: 0.0 }
                        _totalSales.value = total
                    }
                }

            // 3) Cardápios ativos
            db.collection("cardapio")
                .whereEqualTo("ativo", true)
                .addSnapshotListener { snapshot, _ ->
                    _activeMenusCount.value = snapshot?.size() ?: 0
                }

            _loading.value = false
        }
    }

}
