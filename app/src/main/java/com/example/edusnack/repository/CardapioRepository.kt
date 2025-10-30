package com.example.edusnack.repository

import com.example.edusnack.model.Cardapio
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.*

class CardapioRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("cardapios")

    suspend fun adicionarCardapio(cardapio: Cardapio) {
        val docRef = collection.document()
        collection.document(docRef.id).set(cardapio.copy(id = docRef.id)).await()
    }

    suspend fun editarCardapio(cardapio: Cardapio) {
        if (cardapio.id.isNotEmpty()) {
            collection.document(cardapio.id).set(cardapio).await()
        }
    }

    suspend fun listarCardapiosPorData(data: Date): List<Cardapio> {
        val snapshot: QuerySnapshot = collection
            .whereEqualTo("data", data)
            .get()
            .await()
        return snapshot.toObjects(Cardapio::class.java)
    }

    suspend fun listarTodosCardapios(): List<Cardapio> {
        val snapshot = collection.get().await()
        return snapshot.toObjects(Cardapio::class.java)
    }

    suspend fun removerCardapio(cardapioId: String) {
        collection.document(cardapioId).delete().await()
    }
}
