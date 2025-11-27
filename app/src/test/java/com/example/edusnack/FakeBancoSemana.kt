package com.example.edusnack

import com.example.edusnack.model.Semana

/**
 * Pequeno "banco de dados" em memória para testes.
 */
class FakeBancoSemana {

    private val dados = mutableMapOf<String, MutableMap<String, Semana>>()

    fun salvarSemana(alunoId: String, semana: Semana) {
        val mapaAluno = dados.getOrPut(alunoId) { mutableMapOf() }
        mapaAluno[semana.idSemana] = semana
    }

    fun obterSemana(alunoId: String, idSemana: String): Semana? {
        return dados[alunoId]?.get(idSemana)
    }
}
