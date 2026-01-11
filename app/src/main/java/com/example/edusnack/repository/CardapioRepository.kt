package com.example.edusnack.repository

import com.example.edusnack.model.Cardapio
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CardapioRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun listar(): List<Cardapio> {
        return try {
            val snap = db.collection("cardapio").get().await()
            snap.toObjects(Cardapio::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun buscarPorId(id: String): Cardapio? {
        return try {
            db.collection("cardapio").document(id).get().await().toObject(Cardapio::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun adicionar(item: Cardapio): String? {
        return try {
            val doc = db.collection("cardapio").document()
            val comId = item.copy(id = doc.id)
            doc.set(comId).await()
            doc.id
        } catch (e: Exception) {
            null
        }
    }

}
