package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.data.AuthRepository
import com.example.edusnack.model.Aluno
import com.example.edusnack.model.Pedido
import com.example.edusnack.model.StatusPedido
import com.example.edusnack.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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

    private var pedidosListener: ListenerRegistration? = null

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _loading.value = true
            val currentUser = authRepo.getUser()
            _user.value = currentUser

            currentUser?.let { user ->
                fetchAlunoInfo(user.id)
                startPedidosListener(user.id)
            }
            _loading.value = false
        }
    }

    private suspend fun fetchAlunoInfo(userId: String) {
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("alunos")
                .document(userId)
                .get()
                .await()
            
            if (snapshot.exists()) {
                _alunoInfo.value = snapshot.toObject(Aluno::class.java)
            }
        } catch (e: Exception) {
            _alunoInfo.value = null
        }
    }

    private fun startPedidosListener(userId: String) {
        pedidosListener?.remove()

        pedidosListener = FirebaseFirestore.getInstance()
            .collection("pedidos")
            .whereEqualTo("alunoId", userId)
            // Filtramos aqui para garantir que a lista seja atualizada em tempo real
            // assim que o status mudar para ENTREGUE no banco
            .orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                snapshot?.let { docs ->
                    val list = docs.documents.mapNotNull { doc ->
                        val pedido = doc.toObject(Pedido::class.java) ?: return@mapNotNull null
                        
                        // FILTRO: Mostrar apenas pedidos que já foram entregues
                        if (pedido.status != StatusPedido.ENTREGUE) return@mapNotNull null
                        
                        val data = pedido.data.toDate().let { d ->
                            java.text.SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault()).format(d)
                        }
                        
                        val itensNomes = pedido.itens.joinToString(", ") { item -> item.nome }
                        
                        StudentTransaction(
                            title = if (itensNomes.isNotBlank()) itensNomes else "Compra na Cantina",
                            date = data,
                            type = "Pedido #${doc.id.takeLast(4)} - Retirado",
                            amount = -pedido.total
                        )
                    }
                    _transactions.value = list
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        pedidosListener?.remove()
    }
}
