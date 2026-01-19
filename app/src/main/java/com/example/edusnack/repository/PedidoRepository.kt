package com.example.edusnack.repository

import com.example.edusnack.model.Pedido
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

    suspend fun salvarPedido(pedido: Pedido): Result<String> = try {
        val pedidoFinal = preencherDadosAlunoSeFaltando(pedido)
        val doc = pedidos.add(pedidoFinal).await()
        Result.success(doc.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun preencherDadosAlunoSeFaltando(p: Pedido): Pedido {
        if (p.alunoId.isBlank()) return p

        val nomePrecisa = p.alunoNome.isBlank() || p.alunoNome.equals("Aluno", ignoreCase = true)
        val turmaPrecisa = p.turma.isBlank()

        if (!nomePrecisa && !turmaPrecisa) return p

        var nome = p.alunoNome
        var turma = p.turma

        // 0) PRINCIPAL: coleção "usuarios" (seu app salva aluno lá)
        val usuarioSnap = firestore.collection("usuarios").document(p.alunoId).get().await()
        if (usuarioSnap.exists()) {
            if (nomePrecisa) {
                nome = usuarioSnap.getString("nomeCompleto")
                    ?: usuarioSnap.getString("nome")
                            ?: usuarioSnap.getString("name")
                            ?: usuarioSnap.getString("displayName")
                            ?: ""
            }
            if (turmaPrecisa) {
                turma = usuarioSnap.getString("anoOuTurma")
                    ?: usuarioSnap.getString("turma")
                            ?: usuarioSnap.getString("serie")
                            ?: usuarioSnap.getString("serieTurma")
                            ?: ""
            }
        }

        // 1) Fallback: coleção "alunos" (caso exista em algum fluxo antigo)
        if (nome.isBlank() || turma.isBlank()) {
            val alunoSnap = firestore.collection("alunos").document(p.alunoId).get().await()

            if (alunoSnap.exists()) {
                if (nome.isBlank() || nome.equals("Aluno", ignoreCase = true)) {
                    nome = alunoSnap.getString("nome")
                        ?: alunoSnap.getString("name")
                                ?: alunoSnap.getString("nomeCompleto")
                                ?: ""
                }
                if (turma.isBlank()) {
                    turma = alunoSnap.getString("turma")
                        ?: alunoSnap.getString("serie")
                                ?: alunoSnap.getString("serieTurma")
                                ?: ""
                }
            }
        }

        // 2) Fallback: coleção "users" (caso também use em outra parte)
        if (nome.isBlank() || turma.isBlank()) {
            val userSnap = firestore.collection("users").document(p.alunoId).get().await()

            if (nome.isBlank() || nome.equals("Aluno", ignoreCase = true)) {
                nome = userSnap.getString("nome")
                    ?: userSnap.getString("name")
                            ?: userSnap.getString("nomeCompleto")
                            ?: userSnap.getString("displayName")
                            ?: ""
            }
            if (turma.isBlank()) {
                turma = userSnap.getString("turma")
                    ?: userSnap.getString("serie")
                            ?: userSnap.getString("serieTurma")
                            ?: ""
            }
        }

        return p.copy(
            alunoNome = if (nome.isBlank() || nome.equals("Aluno", ignoreCase = true)) "Aluno" else nome,
            turma = turma
        )
    }
}
