package com.example.edusnack.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.*

data class Cardapio(
    @DocumentId
    var id: String = "",
    var data: Timestamp = Timestamp.now(),
    var refeicao: String = "",
    var descricao: String = "",
    var valorNutricional: String = "",
    var autorId: String = ""
){
    fun validarOuErro(): String? {
        if (refeicao.isBlank()) return "Refeição não pode estar em branco"
        if (descricao.isBlank()) return "Descrição não pode estar em branco"
        if (autorId.isBlank()) return "Autor não pode estar em branco"
        return null
    }
}
