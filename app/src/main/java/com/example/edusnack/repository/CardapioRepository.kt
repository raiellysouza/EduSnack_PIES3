package com.example.edusnack.repository

import android.net.Uri
import com.example.edusnack.model.Cardapio
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

class CardapioRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("cardapios")
    private val storage = FirebaseStorage.getInstance("gs://edusnack-1e88c.firebasestorage.app")

    suspend fun adicionarCardapio(cardapio: Cardapio, imagemUri: Uri?): Result<String> {
        cardapio.validarOuErro()?.let {
            return Result.failure(IllegalArgumentException(it))
        }

        return try {
            if (imagemUri != null) {
                val url = uploadImagem(cardapio.autorId, imagemUri)
                cardapio.imagemUrl = url
            }

            cardapio.id = collection.document().id
            cardapio.criadoEm = Timestamp.now()
            cardapio.atualizadoEm = Timestamp.now()

            collection.document(cardapio.id).set(cardapio).await()

            Result.success(cardapio.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun editarCardapio(cardapio: Cardapio, imagemUri: Uri?): Result<Unit> {
        cardapio.validarOuErro()?.let {
            return Result.failure(IllegalArgumentException(it))
        }

        if (cardapio.id.isEmpty()) {
            return Result.failure(IllegalArgumentException("ID vazio"))
        }

        return try {
            if (imagemUri != null) {
                val url = uploadImagem(cardapio.autorId, imagemUri)
                cardapio.imagemUrl = url
            }

            cardapio.atualizadoEm = Timestamp.now()

            collection.document(cardapio.id).set(cardapio).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listarTodos(): Result<List<Cardapio>> {
        return try {
            val snapshot = collection.get().await()
            Result.success(snapshot.toObjects(Cardapio::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarPorId(id: String): Result<Cardapio> {
        if (id.isBlank()) return Result.failure(IllegalArgumentException("ID vazio"))

        return try {
            val doc = collection.document(id).get().await()
            val item = doc.toObject(Cardapio::class.java)

            if (item == null) {
                Result.failure(NoSuchElementException("Cardápio não encontrado"))
            } else {
                Result.success(item)
            }
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

    suspend fun alterarStatus(id: String, ativo: Boolean): Result<Unit> {
        return try {
            collection.document(id).update(
                mapOf(
                    "ativo" to ativo,
                    "atualizadoEm" to Timestamp.now()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadImagem(autorId: String, uri: Uri): String {
        val fileName = "cardapios/$autorId/${UUID.randomUUID()}"
        val ref = storage.reference.child(fileName)

        ref.putFile(uri).await()

        return ref.downloadUrl.await().toString()
    }
}
