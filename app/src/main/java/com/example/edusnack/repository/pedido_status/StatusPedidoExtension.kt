package com.example.edusnack.repository.pedido_status

import com.example.edusnack.model.StatusPedido
import com.example.edusnack.repository.PedidoRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await


suspend fun PedidoRepository.marcarComoPago(id: String): Result<Unit> {
    return try {
        require(id.isNotBlank()) { "ID do pedido obrigatório" }

        val pedido = buscarPorId(id).getOrNull()
            ?: return Result.failure(Exception("Pedido não encontrado"))

        when (pedido!!.status) {
            StatusPedido.ENTREGUE -> return Result.failure(Exception("Pedido já entregue"))
            StatusPedido.CANCELADO -> return Result.failure(Exception("Pedido cancelado"))
            StatusPedido.PAGO -> return Result.failure(Exception("Pedido já está pago"))
            else -> {}
        }

        pedidos.document(id)
            .update(
                "status", StatusPedido.PAGO.name,
                "dataPagamento", Timestamp.now(),
                "dataAtualizacao", Timestamp.now()
            )
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao marcar como pago: ${e.message}"))
    }
}

suspend fun PedidoRepository.marcarComoEntregue(id: String): Result<Unit> {
    return try {
        require(id.isNotBlank()) { "ID do pedido obrigatório" }

        val pedido = buscarPorId(id).getOrNull()
            ?: return Result.failure(Exception("Pedido não encontrado"))

        if (pedido!!.status != StatusPedido.PAGO) {
            return Result.failure(Exception("Só é possível entregar pedidos pagos"))
        }

        pedidos.document(id)
            .update(
                "status", StatusPedido.ENTREGUE.name,
                "dataEntrega", Timestamp.now(),
                "dataAtualizacao", Timestamp.now()
            )
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao marcar como entregue: ${e.message}"))
    }
}

suspend fun PedidoRepository.reverterParaPendente(id: String): Result<Unit> {
    return try {
        require(id.isNotBlank()) { "ID do pedido obrigatório" }

        val pedido = buscarPorId(id).getOrNull()
            ?: return Result.failure(Exception("Pedido não encontrado"))

        when (pedido!!.status) {
            StatusPedido.ENTREGUE -> return Result.failure(Exception("Não é possível reverter pedido entregue"))
            StatusPedido.CANCELADO -> return Result.failure(Exception("Pedido cancelado não pode ser revertido"))
            StatusPedido.PENDENTE -> return Result.failure(Exception("Pedido já está pendente"))
            else -> {}
        }

        pedidos.document(id)
            .update(
                "status", StatusPedido.PENDENTE.name,
                "dataPagamento", null,
                "dataAtualizacao", Timestamp.now()
            )
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao reverter status: ${e.message}"))
    }
}
