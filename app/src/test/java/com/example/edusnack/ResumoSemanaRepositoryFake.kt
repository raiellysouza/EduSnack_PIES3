package com.example.edusnack

import com.example.edusnack.model.Dia
import com.example.edusnack.model.DiaResumo
import com.example.edusnack.model.ResumoSemanal
import com.example.edusnack.model.Semana

class ResumoSemanaRepositoryFake(
    private val db: FakeFirestore
) {

    private fun carregarSemana(alunoId: String, idSemana: String): Semana {
        return db.obterSemana(alunoId, idSemana)
            ?: throw IllegalStateException("Semana não encontrada")
    }

    fun converter(dia: Dia): DiaResumo {
        val total = dia.itens.sumOf { it.preco * it.quantidade }
        return DiaResumo(
            data = dia.data,
            itens = dia.itens,
            totalDia = total,
            quantidadeItens = dia.itens.size
        )
    }

    suspend fun obterResumoSemana(alunoId: String, idSemana: String): Result<ResumoSemanal> {
        return try {
            val semana = carregarSemana(alunoId, idSemana)
            val diasResumo = semana.dias.map { converter(it) }
            val total = diasResumo.sumOf { it.totalDia }
            Result.success(
                ResumoSemanal(
                    idSemana,
                    alunoId,
                    diasResumo,
                    total,
                    diasResumo.size,
                    diasResumo.sumOf { it.quantidadeItens }
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}