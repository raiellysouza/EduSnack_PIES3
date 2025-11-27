package com.example.edusnack

import com.example.edusnack.model.Semana

class FakeFirestore {

    val semanas = mutableMapOf<String, MutableMap<String, Semana>>()

    fun salvarSemana(alunoId: String, semana: Semana) {
        semanas.getOrPut(alunoId) { mutableMapOf() }[semana.idSemana] = semana
    }

    fun obterSemana(alunoId: String, idSemana: String): Semana? {
        return semanas[alunoId]?.get(idSemana)
    }
}