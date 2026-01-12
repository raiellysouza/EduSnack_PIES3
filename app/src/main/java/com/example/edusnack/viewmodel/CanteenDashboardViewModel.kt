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
            
            // Início do dia atual para filtros
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startOfDay = Timestamp(calendar.time)

            // 1. Buscar Pedidos Pendentes e Prontos de HOJE
            db.collection("pedidos")
                .whereGreaterThanOrEqualTo("data", startOfDay)
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.let {
                        val pedidos = it.toObjects(Pedido::class.java)
                        
                        _pendingOrdersCount.value = pedidos.count { p -> p.status == StatusPedido.PENDENTE }
                        _readyOrdersCount.value = pedidos.count { p -> p.status == StatusPedido.PRONTO }
                        
                        // Soma total de vendas do dia (pedidos pagos/entregues/prontos)
                        _totalSales.value = pedidos.sumOf { p -> p.total }
                    }
                }

            // 2. Buscar Cardápios Ativos
            db.collection("cardapio")
                .whereEqualTo("ativo", true)
                .addSnapshotListener { snapshot, _ ->
                    _activeMenusCount.value = snapshot?.size() ?: 0
                }

            _loading.value = false
        }
    }
}
