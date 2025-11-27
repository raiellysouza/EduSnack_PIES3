package com.example.edusnack.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Cardapio(
    @DocumentId
    var id: String = "",
    var nome: String = "",
    var descricao: String = "",
    var categoria: String = "Lanches",
    var preco: Double = 0.0,
    var possuiLactose: Boolean = false,
    var possuiGluten: Boolean = false,
    var vegano: Boolean = false,
    var vegetariano: Boolean = false,
    var calorias: Int? = null,
    var destaque: String? = null,
    var imagemUrl: String? = null,
    var ativo: Boolean = true,
    var autorId: String = "",
    var criadoEm: Timestamp = Timestamp.now(),
    var atualizadoEm: Timestamp = Timestamp.now()
) {
    fun validarOuErro(): String? {
        if (nome.isBlank()) return "Nome não pode estar em branco"
        if (descricao.isBlank()) return "Descrição não pode estar em branco"
        if (preco <= 0.0) return "Preço deve ser maior que zero"
        if (autorId.isBlank()) return "Autor não pode estar em branco"
        return null
    }
}
