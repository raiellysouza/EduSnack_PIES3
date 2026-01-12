package com.example.edusnack

import com.example.edusnack.model.ItemPedido
import com.example.edusnack.model.Semana

/**
 * Fake simples para testes unitários da lógica de montagem de dias.
 * NÃO usa Firestore — usa apenas memória.
 */
class MontagemDiaRepositoryFake(
    private val banco: FakeBancoSemana
) {

    private fun carregarSemana(alunoId: String, idSemana: String): Semana {
        return banco.obterSemana(alunoId, idSemana)
            ?: throw IllegalStateException("Semana não encontrada no FakeBanco")
    }

    private fun salvarSemana(alunoId: String, idSemana: String, semana: Semana) {
        banco.salvarSemana(alunoId, semana)
    }

    suspend fun adicionarItemAoDia(
        alunoId: String,
        idSemana: String,
        dataDia: String,
        item: ItemPedido
    ): Result<Unit> = try {

        val semana = carregarSemana(alunoId, idSemana)

        val dia = semana.dias.find { it.data == dataDia }
            ?: throw IllegalStateException("Dia não encontrado: $dataDia")

        if (dia.itens.any { it.itemId == item.itemId }) {
            throw IllegalStateException("Item já existe no dia")
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

        val semana = carregarSemana(alunoId, idSemana)
        val dia = semana.dias.find { it.data == dataDia }
            ?: throw IllegalStateException("Dia não encontrado")

        val removeu = dia.itens.removeIf { it.itemId == itemId }
        if (!removeu) throw IllegalStateException("Item não encontrado no dia")

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
        quantidade: Int
    ): Result<Unit> = try {

        require(quantidade > 0) { "Quantidade inválida" }

        val semana = carregarSemana(alunoId, idSemana)
        val dia = semana.dias.find { it.data == dataDia }
            ?: throw IllegalStateException("Dia não encontrado")

        val index = dia.itens.indexOfFirst { it.itemId == itemId }
        if (index == -1) throw IllegalStateException("Item não encontrado")

        val antigo = dia.itens[index]
        dia.itens[index] = antigo.copy(quantidade = quantidade)

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

        val semana = carregarSemana(alunoId, idSemana)
        val dia = semana.dias.find { it.data == dataDia }
            ?: throw IllegalStateException("Dia não encontrado")

        dia.itens.clear()
        dia.totalDia = 0.0

        salvarSemana(alunoId, idSemana, semana)

        Result.success(Unit)

    } catch (e: Exception) {
        Result.failure(e)
    }
}
