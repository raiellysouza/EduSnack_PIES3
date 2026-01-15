package com.example.edusnack.repository

import com.example.edusnack.model.Aluno
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Locale

class AlunoRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val alunos get() = firestore.collection("alunos")

    fun getDependentes(responsavelId: String): Flow<List<Aluno>> = callbackFlow {
        val listener = alunos
            .whereEqualTo("responsavelId", responsavelId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val lista = snapshot?.toObjects(Aluno::class.java) ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    suspend fun criarAluno(aluno: Aluno): Result<String> = try {
        validarAluno(aluno)

        val alunoFinal = aluno.copy(
            nomeCompletoLower = aluno.nomeCompleto.lowercase(Locale.ROOT),
            nomeTokens = gerarTokensNome(aluno.nomeCompleto)
        )

        val doc = alunos.add(alunoFinal).await()
        Result.success(doc.id)

    } catch (e: IllegalArgumentException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Atualizar aluno
    suspend fun atualizarAluno(aluno: Aluno): Result<Unit> = try {
        require(aluno.id.isNotBlank()) { "ID do aluno vazio" }
        validarAluno(aluno)

        val alunoFinal = aluno.copy(
            nomeCompletoLower = aluno.nomeCompleto.lowercase(Locale.ROOT),
            nomeTokens = gerarTokensNome(aluno.nomeCompleto)
        )

        alunos.document(aluno.id).set(alunoFinal).await()
        Result.success(Unit)

    } catch (e: IllegalArgumentException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Buscar aluno por ID
    suspend fun buscarPorId(id: String): Result<Aluno?> = try {
        require(id.isNotBlank()) { "ID vazio" }

        val snapshot = alunos.document(id).get().await()
        Result.success(snapshot.toObject(Aluno::class.java))

    } catch (e: Exception) {
        Result.failure(e)
    }

    // Buscar todos de um segmento escolar
    suspend fun buscarPorEtapa(etapa: String): Result<List<Aluno>> = try {
        require(etapa.isNotBlank()) { "Etapa vazia" }

        val snapshot = alunos
            .whereEqualTo("etapa", etapa)
            .get()
            .await()

        Result.success(snapshot.toObjects(Aluno::class.java))

    } catch (e: Exception) {
        Result.failure(e)
    }

    // Buscar por turma/ano
    suspend fun buscarPorTurma(turma: String): Result<List<Aluno>> = try {
        require(turma.isNotBlank()) { "Turma vazia" }

        val snapshot = alunos
            .whereEqualTo("anoOuTurma", turma)
            .get()
            .await()

        Result.success(snapshot.toObjects(Aluno::class.java))

    } catch (e: Exception) {
        Result.failure(e)
    }

    // Busca por nome
    suspend fun buscarPorNome(nome: String): Result<List<Aluno>> = try {
        require(nome.isNotBlank()) { "Nome vazio" }

        val lower = nome.lowercase(Locale.ROOT).trim()

        val snapshot = alunos
            .whereArrayContains("nomeTokens", lower)
            .get()
            .await()

        Result.success(snapshot.toObjects(Aluno::class.java))

    } catch (e: Exception) {
        Result.failure(e)
    }

    // Deletar aluno
    suspend fun deletarAluno(id: String): Result<Unit> = try {
        require(id.isNotBlank()) { "ID vazio" }

        alunos.document(id).delete().await()
        Result.success(Unit)

    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun validarAluno(aluno: Aluno) {
        require(aluno.nomeCompleto.isNotBlank()) { "Nome completo obrigatório" }
        require(aluno.nomeCompleto.length >= 3) { "Nome muito curto" }

        require(aluno.etapa.isNotBlank()) { "Etapa escolar obrigatória" }
        require(aluno.anoOuTurma.isNotBlank()) { "Ano/Turma obrigatório" }

        require(aluno.dataNascimento != null) { "Data de nascimento obrigatória" }

        val idade = calcularIdade(aluno.dataNascimento)
        require(idade in 3..22) { "Idade inválida para contexto escolar" }
    }

    private fun calcularIdade(data: Timestamp): Int {
        val nascimento = data.toDate()
        val hoje = java.util.Date()

        val diff = hoje.time - nascimento.time
        val anos = diff / (1000L * 60 * 60 * 24 * 365)

        return anos.toInt()
    }

    private fun gerarTokensNome(nome: String): List<String> {
        val partes = nome.lowercase(Locale.ROOT).split(" ")
        val tokens = mutableSetOf<String>()

        // tokens individuais
        tokens.addAll(partes)

        // combinações
        for (i in partes.indices) {
            for (j in i + 1 .. partes.lastIndex) {
                tokens.add(partes.subList(i, j).joinToString(" "))
            }
        }

        // nome completo
        tokens.add(nome.lowercase(Locale.ROOT))

        return tokens.toList()
    }
}
