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

class ParentViewModel(
    private val authRepo: AuthRepository = AuthRepository(),
    private val alunoRepo: AlunoRepository = AlunoRepository(),
    private val pedidoRepo: PedidoRepository = PedidoRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _children = MutableStateFlow<List<Aluno>>(emptyList())
    val children: StateFlow<List<Aluno>> = _children

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

            currentUser?.let { parent ->
                fetchChildren(parent.id)
                fetchRecentOrders(parent.id)
            }
            _loading.value = false
        }
    }

    private suspend fun fetchChildren(parentId: String) {
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("alunos")
                .whereEqualTo("responsavelId", parentId)
                .get()
                .await()
            _children.value = snapshot.toObjects(Aluno::class.java)
        } catch (e: Exception) {
            _children.value = emptyList()
        }
    }

    private suspend fun fetchRecentOrders(parentId: String) {
        try {
            // Note: This logic assumes we can find orders by parent. 
            // Usually, we find orders by student. 
            // If children IDs are known, we can query orders where alunoId is in childrenIds.
            val childrenIds = _children.value.map { it.id }
            if (childrenIds.isNotEmpty()) {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("pedidos")
                    .whereIn("alunoId", childrenIds)
                    .orderBy("data", Query.Direction.DESCENDING)
                    .limit(5)
                    .get()
                    .await()
                _recentOrders.value = snapshot.toObjects(Pedido::class.java)
            }
        } catch (e: Exception) {
            _recentOrders.value = emptyList()
        }
    }
}
