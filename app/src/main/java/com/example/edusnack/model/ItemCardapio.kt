package com.example.edusnack.model

import com.google.firebase.firestore.DocumentId

data class ItemCardapio(
    @DocumentId var id: String = "",
    var nome: String = "",
    var descricao: String = "",
    var preco: Double = 0.0,
    var categoria: String = "",   // Sanduíche, Suco, Salgado...
    var imagemUrl: String = "",
    var calorias: Int = 0,
    var alergenicos: List<String> = emptyList(),
    var preparoNaHora: Boolean = false,
    var disponivel: Boolean = true
)
