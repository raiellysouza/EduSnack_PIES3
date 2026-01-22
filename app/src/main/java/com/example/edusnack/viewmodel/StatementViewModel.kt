package com.example.edusnack.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.model.Pedido
import com.example.edusnack.repository.PedidoRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

data class PurchaseTransaction(
    val title: String,
    val studentName: String,
    val date: String,
    val price: String
)

class StatementViewModel(
    private val pedidoRepo: PedidoRepository = PedidoRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _transactions = kotlinx.coroutines.flow.MutableStateFlow<List<PurchaseTransaction>>(emptyList())
    val transactions: kotlinx.coroutines.flow.StateFlow<List<PurchaseTransaction>> = _transactions

    private val _students = kotlinx.coroutines.flow.MutableStateFlow<List<String>>(listOf("Todos"))
    val students: kotlinx.coroutines.flow.StateFlow<List<String>> = _students

    private val _loading = kotlinx.coroutines.flow.MutableStateFlow(false)
    val loading: kotlinx.coroutines.flow.StateFlow<Boolean> = _loading

    init {
        loadData()
    }

    private fun loadData() {
        val userId = auth.currentUser?.uid ?: return
        Log.d("StatementVM", "Responsável logado: $userId")

        viewModelScope.launch {
            _loading.value = true
            try {
                // Busca em 'usuarios' e 'alunos' simultaneamente
                val idsDependentes = mutableSetOf<String>()
                val nomesDependentes = mutableMapOf<String, String>() // ID -> Nome

                val collections = listOf("usuarios", "alunos")
                
                for (col in collections) {
                    val snap = db.collection(col)
                        .whereEqualTo("responsavelId", userId)
                        .get()
                        .await()
                    
                    snap.documents.forEach { doc ->
                        val id = doc.id
                        val nome = doc.getString("nomeCompleto") ?: doc.getString("nome") ?: "Aluno"
                        idsDependentes.add(id)
                        nomesDependentes[id] = nome
                    }
                }

                _students.value = listOf("Todos") + nomesDependentes.values.toList()
                Log.d("StatementVM", "IDs dos dependentes encontrados: $idsDependentes")

                if (idsDependentes.isNotEmpty()) {
                    // Busca pedidos onde o alunoId está na lista de dependentes
                    val pedidosSnap = db.collection("pedidos")
                        .whereIn("alunoId", idsDependentes.toList())
                        .get()
                        .await()
                    
                    val listaPedidos = pedidosSnap.documents.mapNotNull { doc ->
                        try {
                            val pedido = doc.toObject(Pedido::class.java)
                            // Garante que o ID do documento seja setado no objeto se necessário
                            pedido
                        } catch (e: Exception) {
                            Log.e("StatementVM", "Erro ao converter pedido", e)
                            null
                        }
                    }.sortedByDescending { it.data }

                    _transactions.value = formatarPedidos(listaPedidos)
                } else {
                    _transactions.value = emptyList()
                }

            } catch (e: Exception) {
                Log.e("StatementVM", "Erro geral no extrato", e)
            } finally {
                _loading.value = false
            }
        }
    }

    private fun formatarPedidos(pedidos: List<Pedido>): List<PurchaseTransaction> {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return pedidos.map { pedido ->
            val itensNomes = if (pedido.itens.isEmpty()) "Compra na Cantina" 
                            else pedido.itens.joinToString(", ") { it.nome }
            
            PurchaseTransaction(
                title = if (itensNomes.length > 30) itensNomes.take(27) + "..." else itensNomes,
                studentName = pedido.alunoNome.ifBlank { "Aluno" },
                date = sdf.format(pedido.data.toDate()),
                price = "R$ ${String.format("%.2f", pedido.total)}"
            )
        }
    }
}
