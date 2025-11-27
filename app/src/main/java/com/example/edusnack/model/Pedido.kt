package com.example.edusnack.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Pedido(
    @DocumentId
    var id: String = "",
    var usuarioId: String = "",
    var itens: List<CarrinhoItem> = emptyList(),
    var valorTotal: Double = 0.0,
    var status: String = "PENDENTE",
    var criadoEm: Timestamp = Timestamp.now()
)
