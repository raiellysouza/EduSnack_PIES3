package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.model.Cardapio
import com.example.edusnack.repository.CardapioRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class HistoryItem(
    val id: String = "",
    val name: String = "",
    val actionText: String = "",
    val date: String = "",
    val price: String = "",
    val imageUrl: String = ""
)

class CardapioViewModel(
    private val repo: CardapioRepository = CardapioRepository()
) : ViewModel() {

    private val _itens = MutableStateFlow<List<Cardapio>>(emptyList())
    val itens: StateFlow<List<Cardapio>> = _itens

    // Categorias dinâmicas baseadas nos itens do cardápio
    val categorias: StateFlow<List<String>> = _itens
        .map { lista -> 
            lista.map { it.categoria }.distinct().filter { it.isNotBlank() }.sorted()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _historyItems = MutableStateFlow<List<HistoryItem>>(emptyList())
    val historyItems: StateFlow<List<HistoryItem>> = _historyItems

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _itemSelecionado = MutableStateFlow<Cardapio?>(null)
    val itemSelecionado: StateFlow<Cardapio?> = _itemSelecionado

    init {
        carregarItens()
        carregarHistorico()
    }

    fun carregarItens() {
        viewModelScope.launch {
            _loading.value = true
            _itens.value = repo.listar()
            _loading.value = false
        }
    }

    fun carregarItem(id: String) {
        viewModelScope.launch {
            _loading.value = true
            _itemSelecionado.value = repo.buscarPorId(id)
            _loading.value = false
        }
    }

    fun carregarHistorico() {
        viewModelScope.launch {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("historico_cardapio")
                    .orderBy("data", Query.Direction.DESCENDING)
                    .limit(20)
                    .get()
                    .await()
                
                val list = snapshot.documents.mapNotNull { doc ->
                    val data = doc.getTimestamp("data")?.toDate()?.let { 
                        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(it) 
                    } ?: ""
                    
                    HistoryItem(
                        id = doc.id,
                        name = doc.getString("nome") ?: "",
                        actionText = doc.getString("acao") ?: "",
                        date = data,
                        price = "R$ ${String.format("%.2f", doc.getDouble("preco") ?: 0.0)}",
                        imageUrl = doc.getString("imageUrl") ?: ""
                    )
                }
                _historyItems.value = list
            } catch (e: Exception) {
                _historyItems.value = emptyList()
            }
        }
    }

    fun salvarItem(item: Cardapio, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            val id = repo.adicionar(item)
            if (id != null) {
                // Registrar no histórico
                registrarAcaoHistorico(item, "Adicionado por Admin")
                carregarItens()
                onSuccess()
            }
            _loading.value = false
        }
    }

    private suspend fun registrarAcaoHistorico(item: Cardapio, acao: String) {
        try {
            val historico = hashMapOf(
                "nome" to item.nome,
                "acao" to acao,
                "preco" to (item.preco ?: 0.0),
                "data" to com.google.firebase.Timestamp.now(),
                "imageUrl" to (item.imagemUrl ?: "")
            )
            FirebaseFirestore.getInstance().collection("historico_cardapio").add(historico).await()
            carregarHistorico()
        } catch (e: Exception) {
            // Log error
        }
    }
}
