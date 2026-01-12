package com.example.edusnack.repository

import com.example.edusnack.model.Pedido
import com.example.edusnack.model.StatusPedido
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    /**
     * Salva em /pedidos/{id} e garante o ID dentro do documento
     */
    suspend fun salvarPedido(pedido: Pedido): Result<String> = try {
        val docRef = pedidos.document()
        val id = docRef.id

        val pedidoComId = pedido.copy(id = id)
        docRef.set(pedidoComId).await()

        Result.success(id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Cantineiro: observar pedidos por status em tempo real
     */
    fun observarPedidosPorStatus(status: StatusPedido): Flow<List<Pedido>> = callbackFlow {
        val query = pedidos
            .whereEqualTo("status", status)
            .orderBy("data", Query.Direction.DESCENDING)

        val reg = query.addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }

            val lista = snap?.toObjects(Pedido::class.java) ?: emptyList()
            trySend(lista)
        }

        awaitClose { reg.remove() }
    }

    /**
     * Atualiza status do pedido (cantineiro)
     */
    suspend fun atualizarStatus(pedidoId: String, novo: StatusPedido): Result<Unit> = try {
        pedidos.document(pedidoId).update("status", novo).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
