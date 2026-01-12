package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.data.AuthRepository
import com.example.edusnack.model.Aluno
import com.example.edusnack.repository.AlunoRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreditViewModel(
    private val authRepo: AuthRepository = AuthRepository(),
    private val alunoRepo: AlunoRepository = AlunoRepository()
) : ViewModel() {

    private val _children = MutableStateFlow<List<Aluno>>(emptyList())
    val children: StateFlow<List<Aluno>> = _children

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
                    val snapshot = FirebaseFirestore.getInstance()
                        .collection("alunos")
                        .whereEqualTo("responsavelId", parent.id)
                        .get()
                        .await()
                    _children.value = snapshot.toObjects(Aluno::class.java)
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
                // Aqui seria a integração real com um gateway de pagamento.
                // Para fins de demonstração, simulamos o sucesso após o "pagamento".
                // Em um cenário real, você atualizaria o saldo do aluno no Firestore.
                
                val alunoDoc = FirebaseFirestore.getInstance().collection("alunos").document(alunoId)
                FirebaseFirestore.getInstance().runTransaction { transaction ->
                    val snapshot = transaction.get(alunoDoc)
                    val saldoAtual = snapshot.getDouble("saldo") ?: 0.0
                    transaction.update(alunoDoc, "saldo", saldoAtual + amount)
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
