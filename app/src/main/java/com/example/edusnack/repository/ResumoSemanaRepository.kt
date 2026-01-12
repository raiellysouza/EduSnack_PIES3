package com.example.edusnack.repository

import com.example.edusnack.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ResumoSemanaRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val diasReservados get() = firestore.collection("dias_reservados")

    suspend fun obterResumoSemana(
        alunoId: String,
        idSemana: String
    ): Result<ResumoSemanal> = try {
        val semana = getSemana(alunoId, idSemana).getOrThrow()

        val diasResumo = semana.dias.map { dia ->
            converterParaDiaResumo(dia)
        }

        val totalGeral = diasResumo.sumOf { it.totalDia }
        val qtdItens = diasResumo.sumOf { it.quantidadeItens }

        Result.success(
            ResumoSemanal(
                idSemana = idSemana,
                alunoId = alunoId,
                dias = diasResumo,
                totalGeral = totalGeral,
                quantidadeDias = diasResumo.size,
                quantidadeItensTotal = qtdItens
            )
        )

    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun obterTotalSemanal(
        alunoId: String,
        idSemana: String
    ): Result<Double> = try {
        val semana = getSemana(alunoId, idSemana).getOrThrow()

        val total = semana.dias.sumOf { dia ->
            dia.itens.sumOf { it.preco?.times(it.quantidade) ?: 0.0 }
        }

        Result.success(total)

    } catch (e: Exception) {
        Result.failure(e)
    }


    private suspend fun getSemana(
        alunoId: String,
        idSemana: String
    ): Result<Semana> {
        return try {

            val snap = diasReservados
                .document(alunoId)
                .collection("semanas")
                .document(idSemana)
                .get()
                .await()

            if (!snap.exists())
                return Result.failure(Exception("Semana não encontrada"))

            val semana = snap.toObject(Semana::class.java)
                ?: return Result.failure(Exception("Erro ao desserializar semana"))

            Result.success(semana)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun converterParaDiaResumo(dia: Dia): DiaResumo {
        val total = dia.itens.sumOf { it.preco?.times(it.quantidade) ?: 0.0 }

        return DiaResumo(
            data = dia.data,
            itens = dia.itens,
            totalDia = total,
            quantidadeItens = dia.itens.size
        )
    }
}

