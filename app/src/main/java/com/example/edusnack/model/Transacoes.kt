package com.example.edusnack.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp


data class Transacoes (
    @DocumentId var id: String = "",
    var tipo: String = "",
    var valor: Double = 0.0,
    var criadoEm: Timestamp = Timestamp.now(),
    var alunoId: String = "",
    var descricao: String? = ""
)