package com.example.edusnack.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReservaSemanaRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val diasReservados get() = firestore.collection("dias_reservados")

    suspend fun reservarDiasSemana(
        alunoId: String,
        idSemana: String,
        dias: List<String>
    ): Result<Unit> = try {

        require(dias.isNotEmpty()) { "Nenhum dia selecionado" }

        val listaDias = dias.map { data ->
            mapOf(
                "data" to data,
                "itens" to emptyList<Map<String, Any>>(),
                "totalDia" to 0.0
            )
        }

        val doc = diasReservados
            .document(alunoId)
            .collection("semanas")
            .document(idSemana)

        val body = mapOf(
            "idSemana" to idSemana,
            "dias" to listaDias,
            "status" to "SELECIONANDO_ITENS",
            "criadoEm" to Timestamp.now()
        )

        doc.set(body).await()

        Result.success(Unit)

    } catch (e: Exception) {
        Result.failure(e)
    }


    suspend fun obterSemana(
        alunoId: String,
        idSemana: String
    ): Result<Map<String, Any>?> = try {

        val snap = diasReservados
            .document(alunoId)
            .collection("semanas")
            .document(idSemana)
            .get()
            .await()

        Result.success(snap.data)

    } catch (e: Exception) {
        Result.failure(e)
    }
}
