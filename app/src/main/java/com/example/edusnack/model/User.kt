package com.example.edusnack.model

data class User(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val tipo: String = "", // aluno | responsavel | cantina
    val fotoUrl: String = "",
    val responsavelId: String? = null // ID do pai/mãe vinculado
)
