package com.example.edusnack.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class CardapioRepository {

    private val collection = FirebaseFirestore.getInstance().collection("cardapios")

    suspend fun adicionarCardapio(cardapio: `Cardapio.kt`): Result<String> {
        cardapio.validarOuErro()?.let {
            return Result.failure(IllegalArgumentException(it))
        }

        return try {
            val docRef = collection.add(cardapio).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun editarCardapio(cardapio: `Cardapio.kt`): Result<Unit> {
        cardapio.validarOuErro()?.let {
            return Result.failure(IllegalArgumentException(it))
        }

        if (cardapio.id.isEmpty()) {
            return Result.failure(IllegalArgumentException("ID vazio"))
        }

        return try {
            collection.document(cardapio.id).set(cardapio).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Essa função só funciona se o Timestamp for exato
    suspend fun listarCardapiosPorData(data: Timestamp): Result<List<`Cardapio.kt`>> {
        return try {
            val snapshot = collection
                .whereEqualTo("data", data)
                .get()
                .await()
            Result.success(snapshot.toObjects(`Cardapio.kt`::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listarTodosCardapios(): Result<List<`Cardapio.kt`>> {
        return try {
            val snapshot = collection.get().await()
            Result.success(snapshot.toObjects(`Cardapio.kt`::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarCardapioPorId(id: String): Result<`Cardapio.kt`> {
        return try {
            val doc = collection.document(id).get().await()
            doc.toObject(`Cardapio.kt`::class.java)?.let {
                Result.success(it)
            } ?: Result.failure(NoSuchElementException("Cardápio não encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removerCardapio(id: String): Result<Unit> {
        if (id.isEmpty()) {
            return Result.failure(IllegalArgumentException("ID vazio"))
        }

        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
