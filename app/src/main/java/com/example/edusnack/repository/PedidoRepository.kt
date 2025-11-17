package com.example.edusnack.repository

import com.example.edusnack.model.Pedido
import com.example.edusnack.model.StatusPedido
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PedidoRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val pedidos = firestore.collection("pedidos")

    suspend fun buscarPorId(id: String): Result<Pedido?> = try {
        val snap = pedidos.document(id).get().await()
        Result.success(snap.toObject(Pedido::class.java))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun salvarPedido (pedido: Pedido): Result<String> = try {
        pedidos.document().collection("pedidos").add(pedido).await()
        Result.success(pedido.id)
    } catch (e: Exception) {
        Result.failure(e)
    }


}
