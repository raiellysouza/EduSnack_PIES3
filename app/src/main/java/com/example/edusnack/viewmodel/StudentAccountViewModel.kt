package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.data.AuthRepository
import com.example.edusnack.model.Aluno
import com.example.edusnack.model.Pedido
import com.example.edusnack.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class StudentTransaction(
    val title: String,
    val date: String,
    val type: String,
    val amount: Double
)

class StudentAccountViewModel(
    private val authRepo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _alunoInfo = MutableStateFlow<Aluno?>(null)
    val alunoInfo: StateFlow<Aluno?> = _alunoInfo

    private val _transactions = MutableStateFlow<List<StudentTransaction>>(emptyList())
    val transactions: StateFlow<List<StudentTransaction>> = _transactions

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

            currentUser?.let { user ->
                // Se o usuário logado for do tipo 'aluno', buscamos os detalhes dele na coleção 'alunos'
                // Aqui assumimos que o ID do User é o mesmo ID do Aluno ou buscamos por um campo específico.
                // No seu projeto, parece que alunos e usuários são entidades relacionadas.
                fetchAlunoInfo(user.id)
                fetchTransactions(user.id)
            }
            _loading.value = false
        }
    }

    private suspend fun fetchAlunoInfo(userId: String) {
        try {
            // Tenta buscar na coleção 'alunos' usando o ID do usuário autenticado
            val snapshot = FirebaseFirestore.getInstance()
                .collection("alunos")
                .document(userId)
                .get()
                .await()
            
            if (snapshot.exists()) {
                _alunoInfo.value = snapshot.toObject(Aluno::class.java)
            } else {
                // Caso não encontre pelo ID direto, pode ser que precise buscar por um campo 'userId'
                // ou simplesmente usar os dados do perfil básico.
            }
        } catch (e: Exception) {
            _alunoInfo.value = null
        }
    }

    private suspend fun fetchTransactions(userId: String) {
        try {
            // Buscamos os pedidos realizados pelo aluno para montar o histórico de compras
            val snapshot = FirebaseFirestore.getInstance()
                .collection("pedidos")
                .whereEqualTo("alunoId", userId)
                .orderBy("data", Query.Direction.DESCENDING)
                .get()
                .await()

            val list = snapshot.documents.mapNotNull { doc ->
                val data = doc.getTimestamp("data")?.toDate()?.let { 
                    java.text.SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault()).format(it) 
                } ?: ""
                
                StudentTransaction(
                    title = "Compra na Cantina",
                    date = data,
                    type = "Pedido #${doc.id.takeLast(4)}",
                    amount = -(doc.getDouble("total") ?: 0.0)
                )
            }
            _transactions.value = list
            
            // Nota: Depósitos (Adicionar Crédito) deveriam estar em uma coleção de 'transacoes'
            // para serem listados aqui também. Se existirem, você faria um merge das listas.
        } catch (e: Exception) {
            _transactions.value = emptyList()
        }
    }
}
