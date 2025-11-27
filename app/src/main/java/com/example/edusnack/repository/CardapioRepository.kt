package com.example.edusnack.repository

import android.net.Uri
import com.example.edusnack.model.Cardapio
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

class CardapioRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    private val col = firestore.collection("cardapios")

    suspend fun adicionarCardapio(cardapio: Cardapio, imagemUri: Uri?): Result<String> {
        return try {
            if (imagemUri != null) {
                cardapio.imagemUrl = uploadImagem(cardapio.autorId, imagemUri)
            }
            val id = col.document().id
            cardapio.id = id
            cardapio.criadoEm = Timestamp.now()
            cardapio.atualizadoEm = Timestamp.now()
            col.document(id).set(cardapio).await()
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun editarCardapio(cardapio: Cardapio, imagemUri: Uri?): Result<Unit> {
        return try {
            if (imagemUri != null) {
                cardapio.imagemUrl = uploadImagem(cardapio.autorId, imagemUri)
            }
            cardapio.atualizadoEm = Timestamp.now()
            col.document(cardapio.id).set(cardapio).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listarTodos(): Result<List<Cardapio>> {
        return try {
            val snap = col.get().await()
            Result.success(snap.toObjects(Cardapio::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarPorId(id: String): Result<Cardapio> {
        return try {
            val doc = col.document(id).get().await()
            val c = doc.toObject(Cardapio::class.java)
            if (c != null) Result.success(c) else Result.failure(NoSuchElementException("Não encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun remover(id: String): Result<Unit> {
        return try {
            col.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun alterarStatus(id: String, ativo: Boolean): Result<Unit> {
        return try {
            col.document(id).update(mapOf("ativo" to ativo, "atualizadoEm" to Timestamp.now())).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadImagem(autorId: String, uri: Uri): String {
        val name = "cardapios/$autorId/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(name)
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}
