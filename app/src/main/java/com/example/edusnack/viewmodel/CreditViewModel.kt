package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.data.AuthRepository
import com.example.edusnack.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreditViewModel(
    private val authRepo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _children = MutableStateFlow<List<User>>(emptyList())
    val children: StateFlow<List<User>> = _children

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    init {
        loadChildren()
    }

    fun loadChildren() {
        viewModelScope.launch {
            _loading.value = true
            val currentUser = authRepo.getUser()
            currentUser?.let { parent ->
                try {
                    // Busca dependentes na coleção "usuarios" onde responsavelId é o ID do pai
                    val snapshot = FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .whereEqualTo("responsavelId", parent.id)
                        .whereEqualTo("tipo", "aluno")
                        .get()
                        .await()
                    _children.value = snapshot.toObjects(User::class.java)
                } catch (e: Exception) {
                    _children.value = emptyList()
                }
            }
            _loading.value = false
        }
    }

    fun addCredit(alunoId: String, amount: Double) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val db = FirebaseFirestore.getInstance()
                // Usamos o ID do usuário aluno para atualizar o saldo na coleção "usuarios"
                val alunoRef = db.collection("usuarios").document(alunoId)
                
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(alunoRef)
                    // Verifica se o campo "saldo" existe, caso contrário assume 0.0
                    val saldoAtual = snapshot.getDouble("saldo") ?: 0.0
                    transaction.update(alunoRef, "saldo", saldoAtual + amount)
                }.await()

                _success.value = true
            } catch (e: Exception) {
                _success.value = false
            }
            _loading.value = false
        }
    }
    
    fun resetSuccess() {
        _success.value = false
    }
}
