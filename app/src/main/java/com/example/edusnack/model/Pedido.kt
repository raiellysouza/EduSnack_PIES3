package com.example.edusnack.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Pedido(
    @DocumentId val id: String = "",
    val alunoId: String = "",
    val alunoNome: String = "",
    val turma: String = "",
    val itens: List<ItemPedido> = emptyList(),
    val data: Timestamp = Timestamp.now(),
    var status: StatusPedido = StatusPedido.PENDENTE,
    val total: Double = 0.0,
    val codigoRetirada: String = ""
)
