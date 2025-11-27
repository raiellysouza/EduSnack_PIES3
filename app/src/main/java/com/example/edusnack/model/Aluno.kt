package com.example.edusnack.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Aluno(
    @DocumentId var id: String = "",
    var nomeCompleto: String = "",
    var nomeCompletoLower: String = "",
    var nomeTokens: List<String> = emptyList(),
    var etapa: String = "",
    var anoOuTurma: String = "",
    var dataNascimento: Timestamp = Timestamp.now(),
    var responsavelId: String = "",
)

