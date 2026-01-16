package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.data.AuthRepository
import com.example.edusnack.model.Aluno
import com.example.edusnack.model.Pedido
import com.example.edusnack.model.User
import com.example.edusnack.repository.AlunoRepository
import com.example.edusnack.repository.PedidoRepository
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
    private val authRepo: AuthRepository = AuthRepository(),
    private val alunoRepo: AlunoRepository = AlunoRepository(),
    private val pedidoRepo: PedidoRepository = PedidoRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // Mantém caso você ainda use em algum lugar
    private val _children = MutableStateFlow<List<Aluno>>(emptyList())
    val children: StateFlow<List<Aluno>> = _children

    // IDs reais dos docs do Firestore (pra queries)
    private val _childrenIds = MutableStateFlow<List<String>>(emptyList())

    // Lista pronta pra UI (nome/turma/saldo)
    private val _dependentsUi = MutableStateFlow<List<DependentUi>>(emptyList())
    val dependentsUi: StateFlow<List<DependentUi>> = _dependentsUi

    // Conta (5 itens)
    private val _recentOrders = MutableStateFlow<List<Pedido>>(emptyList())
    val recentOrders: StateFlow<List<Pedido>> = _recentOrders

    // Extrato (todos)
    private val _allOrders = MutableStateFlow<List<Pedido>>(emptyList())
    val allOrders: StateFlow<List<Pedido>> = _allOrders

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

            // 1) carrega dependentes (e IDs)
            fetchChildren(currentUser.id)

            // 2) com childrenIds definidos, carrega pedidos
            fetchRecentOrders()
            loadAllOrders()

            _loading.value = false
        }
    }

    fun refreshOrders() {
        viewModelScope.launch {
            fetchRecentOrders()
            loadAllOrders()
        }
    }

    fun loadAllOrders() {
        viewModelScope.launch {
            try {
                val childrenIds = _childrenIds.value
                if (childrenIds.isEmpty()) {
                    _allOrders.value = emptyList()
                    return@launch
                }

                val snapshot = FirebaseFirestore.getInstance()
                    .collection("pedidos")
                    .whereIn("alunoId", childrenIds)
                    .orderBy("data", Query.Direction.DESCENDING)
                    .get()
                    .await()

                _allOrders.value = snapshot.toObjects(Pedido::class.java)
            } catch (_: Exception) {
                _allOrders.value = emptyList()
            }
        }
    }

    private suspend fun fetchChildren(parentId: String) {
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("alunos")
                .whereEqualTo("responsavelId", parentId)
                .get()
                .await()

            // IDs reais do Firestore
            _childrenIds.value = snapshot.documents.map { it.id }

            // objetos (se você ainda usa o model Aluno em algum lugar)
            _children.value = snapshot.toObjects(Aluno::class.java)

            // UI pronta (não depende do model Aluno ter "id", "saldo", etc)
            _dependentsUi.value = snapshot.documents.map { doc ->
                val nome = doc.getString("nomeCompleto")
                    ?: doc.getString("nome")
                    ?: "Aluno"

                val turma = doc.getString("anoOuTurma")
                    ?: doc.getString("turma")
                    ?: "-"

                val saldo = doc.getDouble("saldo")
                    ?: doc.getDouble("carteira")
                    ?: 0.0

                val fotoUrl = doc.getString("fotoUrl")
                    ?: doc.getString("photoUrl")

                DependentUi(
                    id = doc.id,
                    nome = nome,
                    turma = turma,
                    saldo = saldo,
                    fotoUrl = fotoUrl
                )
            }
        } catch (_: Exception) {
            _children.value = emptyList()
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
        _children.value = emptyList()
        _childrenIds.value = emptyList()
        _dependentsUi.value = emptyList()
        _recentOrders.value = emptyList()
        _allOrders.value = emptyList()
    }
}
