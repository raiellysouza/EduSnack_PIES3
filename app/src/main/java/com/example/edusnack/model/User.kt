package com.example.edusnack.model

data class Usuario(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val tipo: TipoUsuario = TipoUsuario.ALUNO,
    val saldo: Double = 0.0
)

enum class TipoUsuario {
    ALUNO,
    RESPONSAVEL,
    CANTINA
}
