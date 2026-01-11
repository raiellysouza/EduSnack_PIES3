package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusnack.model.Cardapio
import com.example.edusnack.repository.CardapioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardapioViewModel(
    private val repo: CardapioRepository = CardapioRepository()
) : ViewModel() {

    private val _itens = MutableStateFlow<List<Cardapio>>(emptyList())
    val itens = _itens.asStateFlow()

    private val _itemSelecionado = MutableStateFlow<Cardapio?>(null)
    val itemSelecionado = _itemSelecionado.asStateFlow()

    init {
        carregarItens()
    }

    fun carregarItens() {
        viewModelScope.launch {
            _itens.value = repo.listar()
        }
    }

    fun carregarItem(id: String) {
        viewModelScope.launch {
            _itemSelecionado.value = repo.buscarPorId(id)
        }
    }

    fun salvarItem(item: Cardapio, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val id = repo.adicionar(item)
            if (id != null) {
                carregarItens()
                onSuccess()
            } else {
                onError("Não foi possível salvar o item.")
            }
        }
    }

}
