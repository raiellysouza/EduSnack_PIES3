package com.example.edusnack.repository

import com.example.edusnack.model.ItemPedido
import com.example.edusnack.model.Semana
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MontagemDiaRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val diasReservados get() = firestore.collection("dias_reservados")

    private suspend fun carregarSemana(
        alunoId: String,
        idSemana: String
    ): Semana {
        validarIds(alunoId, idSemana)

        val snap = diasReservados
            .document(alunoId)
            .collection("semanas")
            .document(idSemana)
            .get()
            .await()

        if (!snap.exists()) {
            throw Exception("Semana não encontrada para o aluno $alunoId")
        }

        return snap.toObject(Semana::class.java)
            ?: throw Exception("Erro ao desserializar semana")
    }

    private suspend fun salvarSemana(
        alunoId: String,
        idSemana: String,
        semana: Semana
    ) {
        diasReservados
            .document(alunoId)
            .collection("semanas")
            .document(idSemana)
            .set(semana)
            .await()
    }
    suspend fun adicionarItemAoDia(
        alunoId: String,
        idSemana: String,
        dataDia: String,
        item: ItemPedido
    ): Result<Unit> = try {

        validarEntradaCompleta(alunoId, idSemana, dataDia, item)

        val semana = carregarSemana(alunoId, idSemana)

        val dia = semana.dias.find { it.data == dataDia }
            ?: throw Exception("Dia $dataDia não encontrado na semana")

        if (dia.itens.any { it.itemId == item.itemId }) {
            throw Exception("Item já existe neste dia")
        }

        dia.itens.add(item.copy())

        dia.totalDia = dia.itens.sumOf { it.preco?.times(it.quantidade) ?: 0.0 }

        salvarSemana(alunoId, idSemana, semana)

        Result.success(Unit)

    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun removerItemDoDia(
        alunoId: String,
        idSemana: String,
        dataDia: String,
        itemId: String
    ): Result<Unit> = try {

        validarIds(alunoId, idSemana)
        require(dataDia.isNotBlank()) { "Data obrigatória" }
        require(itemId.isNotBlank()) { "ID do item obrigatório" }

        val semana = carregarSemana(alunoId, idSemana)

        val dia = semana.dias.find { it.data == dataDia }
            ?: throw Exception("Dia não encontrado")

        val removido = dia.itens.removeIf { it.itemId == itemId }

        if (!removido) throw Exception("Item não encontrado")

        dia.totalDia = dia.itens.sumOf { it.preco?.times(it.quantidade) ?: 0.0 }

        salvarSemana(alunoId, idSemana, semana)

        Result.success(Unit)

    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun atualizarQuantidadeItem(
        alunoId: String,
        idSemana: String,
        dataDia: String,
        itemId: String,
        novaQuantidade: Int
    ): Result<Unit> = try {

        validarIds(alunoId, idSemana)
        require(dataDia.isNotBlank()) { "Data obrigatória" }
        require(itemId.isNotBlank()) { "ID do item obrigatório" }
        require(novaQuantidade > 0) { "Quantidade deve ser maior que 0" }

        val semana = carregarSemana(alunoId, idSemana)

        val dia = semana.dias.find { it.data == dataDia }
            ?: throw Exception("Dia não encontrado")

        val index = dia.itens.indexOfFirst { it.itemId == itemId }
        if (index == -1) throw Exception("Item não encontrado")

        dia.itens[index] = dia.itens[index].copy(quantidade = novaQuantidade)

        dia.totalDia = dia.itens.sumOf { it.preco?.times(it.quantidade) ?: 0.0 }

        salvarSemana(alunoId, idSemana, semana)

        Result.success(Unit)

    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun limparDia(
        alunoId: String,
        idSemana: String,
        dataDia: String
    ): Result<Unit> = try {

        validarIds(alunoId, idSemana)
        require(dataDia.isNotBlank()) { "Data obrigatória" }

        val semana = carregarSemana(alunoId, idSemana)

        val dia = semana.dias.find { it.data == dataDia }
            ?: throw Exception("Dia não encontrado")

        dia.itens.clear()
        dia.totalDia = 0.0

        salvarSemana(alunoId, idSemana, semana)

        Result.success(Unit)

    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun buscarSemana(
        alunoId: String,
        idSemana: String
    ): Result<Semana?> = try {

        Result.success(carregarSemana(alunoId, idSemana))

    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun listarSemanasDoAluno(alunoId: String): Result<List<Semana>> = try {

        require(alunoId.isNotBlank()) { "ID do aluno obrigatório" }

        val snap = diasReservados
            .document(alunoId)
            .collection("semanas")
            .get()
            .await()

        Result.success(snap.toObjects(Semana::class.java))

    } catch (e: Exception) {
        Result.failure(e)
    }


    private fun validarIds(alunoId: String, idSemana: String) {
        require(alunoId.isNotBlank()) { "ID do aluno obrigatório" }
        require(idSemana.isNotBlank()) { "ID da semana obrigatório" }
    }

    private fun validarEntradaCompleta(
        alunoId: String,
        idSemana: String,
        dataDia: String,
        item: ItemPedido
    ) {
        validarIds(alunoId, idSemana)
        require(dataDia.isNotBlank()) { "Data obrigatória" }
        require(item.itemId.isNotBlank()) { "ID do item obrigatório" }
        require(item.nome.isNotBlank()) { "Nome obrigatório" }
        item.preco?.let { require(it > 0) { "Preço deve ser maior que 0" } }
        require(item.quantidade > 0) { "Quantidade deve ser maior que 0" }
    }
}

