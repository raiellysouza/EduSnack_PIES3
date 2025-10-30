package com.example.edusnack.model

data class Usuario(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val tipo: TipoUsuario = TipoUsuario.ALUNO
)

enum class TipoUsuario {
    ALUNO,
    RESPONSAVEL,
    NUTRICIONISTA,
    ESCOLA
}
