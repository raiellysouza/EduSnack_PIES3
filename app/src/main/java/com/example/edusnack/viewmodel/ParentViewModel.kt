package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.data.AuthRepository
import com.example.edusnack.model.Pedido
import com.example.edusnack.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class DependentUi(
    val id: String,
    val nome: String,
    val turma: String,
    val saldo: Double,
    val fotoUrl: String? = null
)

class ParentViewModel(
    private val authRepo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _childrenIds = MutableStateFlow<List<String>>(emptyList())

    private val _dependentsUi = MutableStateFlow<List<DependentUi>>(emptyList())
    val dependentsUi: StateFlow<List<DependentUi>> = _dependentsUi

    private val _recentOrders = MutableStateFlow<List<Pedido>>(emptyList())
    val recentOrders: StateFlow<List<Pedido>> = _recentOrders

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _loading.value = true

            val currentUser = authRepo.getUser()
            _user.value = currentUser

            if (currentUser == null) {
                clearAll()
                _loading.value = false
                return@launch
            }

            // 1) Carrega dependentes da coleção "usuarios" (onde o vínculo é salvo)
            fetchChildrenFromUsuarios(currentUser.id)

            // 2) Carrega pedidos recentes dos filhos
            fetchRecentOrders()

            _loading.value = false
        }
    }

    private suspend fun fetchChildrenFromUsuarios(parentId: String) {
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("usuarios")
                .whereEqualTo("responsavelId", parentId)
                .whereEqualTo("tipo", "aluno")
                .get()
                .await()

            _childrenIds.value = snapshot.documents.map { it.id }

            _dependentsUi.value = snapshot.documents.map { doc ->
                DependentUi(
                    id = doc.id,
                    nome = doc.getString("nome") ?: "Aluno",
                    turma = doc.getString("anoOuTurma") ?: doc.getString("turma") ?: "-",
                    saldo = doc.getDouble("saldo") ?: 0.0,
                    fotoUrl = doc.getString("fotoUrl")
                )
            }
        } catch (_: Exception) {
            _childrenIds.value = emptyList()
            _dependentsUi.value = emptyList()
        }
    }

    private suspend fun fetchRecentOrders() {
        try {
            val childrenIds = _childrenIds.value
            if (childrenIds.isEmpty()) {
                _recentOrders.value = emptyList()
                return
            }

            val snapshot = FirebaseFirestore.getInstance()
                .collection("pedidos")
                .whereIn("alunoId", childrenIds)
                .orderBy("data", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .await()

            _recentOrders.value = snapshot.toObjects(Pedido::class.java)
        } catch (_: Exception) {
            _recentOrders.value = emptyList()
        }
    }

    private fun clearAll() {
        _childrenIds.value = emptyList()
        _dependentsUi.value = emptyList()
        _recentOrders.value = emptyList()
    }
}
