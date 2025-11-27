package com.example.edusnack.repository

import com.example.edusnack.model.Pedido
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PedidoRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun salvarPedido(pedido: Pedido): String? {
        return try {
            val ref = db.collection("pedidos").add(pedido).await()
            ref.id
        } catch (e: Exception) {
            null
        }
    }
}
