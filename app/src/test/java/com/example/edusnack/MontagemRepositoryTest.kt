package com.example.edusnack

import com.example.edusnack.model.Dia
import com.example.edusnack.model.ItemPedido
import com.example.edusnack.model.Semana
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class MontagemRepositoryTest {

    /** Banco fake para evitar Firestore */
    private val fakeDb = FakeBancoSemana()

    /** Cria uma semana inicial de teste */
    private fun criarSemanaBase(): Semana {
        return Semana(
            idSemana = "SEM01",
            alunoId = "A1",
            dias = mutableListOf(
                Dia(
                    data = "2025-11-20",
                ),
                Dia(
                    data = "2025-11-21",
                )
            )
        )
    }

    /** Testa adicionar item */
    @Test
    fun deveAdicionarItemNoDia() = runBlocking {
        val semana = criarSemanaBase()
        fakeDb.salvarSemana("A1", semana)

        val repo = MontagemDiaRepositoryFake(fakeDb)

        repo.adicionarItemAoDia(
            alunoId = "A1",
            idSemana = "SEM01",
            dataDia = "2025-11-20",
            item = ItemPedido("10", "Salgado", 5.0, 1)
        )

        val atualizado = fakeDb.obterSemana("A1", "SEM01")!!
        val dia = atualizado.dias.first()

        assertEquals(1, dia.itens.size)
        assertEquals(5.0, dia.totalDia, 0.01)
    }

    /** Testa aumento de quantidade */
    @Test
    fun deveAtualizarQuantidadeDoItem() = runBlocking {
        val semana = criarSemanaBase()
        semana.dias[0].itens.add(ItemPedido("10", "Salgado", 5.0, 1))
        fakeDb.salvarSemana("A1", semana)

        val repo = MontagemDiaRepositoryFake(fakeDb)

        repo.atualizarQuantidadeItem(
            alunoId = "A1",
            idSemana = "SEM01",
            dataDia = "2025-11-20",
            itemId = "10",
            quantidade = 3
        )

        val atualizada = fakeDb.obterSemana("A1", "SEM01")!!
        val dia = atualizada.dias[0]

        assertEquals(3, dia.itens.first().quantidade)
        assertEquals(15.0, dia.totalDia, 0.01)
    }

    /** Testa remoção */
    @Test
    fun deveRemoverItemDoDia() = runBlocking {
        val semana = criarSemanaBase()
        semana.dias[0].itens.add(ItemPedido("99", "Suco", 3.0, 1))
        fakeDb.salvarSemana("A1", semana)

        val repo = MontagemDiaRepositoryFake(fakeDb)

        repo.removerItemDoDia(
            alunoId = "A1",
            idSemana = "SEM01",
            dataDia = "2025-11-20",
            itemId = "99"
        )

        val atualizada = fakeDb.obterSemana("A1", "SEM01")!!
        val dia = atualizada.dias[0]

        assertTrue(dia.itens.isEmpty())
        assertEquals(0.0, dia.totalDia, 0.01)
    }

    /** Testa limpar o dia */
    @Test
    fun deveLimparDia() = runBlocking {
        val semana = criarSemanaBase()
        semana.dias[0].itens.add(ItemPedido("22", "Bolo", 4.0, 1))
        semana.dias[0].itens.add(ItemPedido("33", "Café", 2.0, 1))
        fakeDb.salvarSemana("A1", semana)

        val repo = MontagemDiaRepositoryFake(fakeDb)

        repo.limparDia(
            alunoId = "A1",
            idSemana = "SEM01",
            dataDia = "2025-11-20"
        )

        val atualizada = fakeDb.obterSemana("A1", "SEM01")!!
        val dia = atualizada.dias[0]

        assertEquals(0, dia.itens.size)
        assertEquals(0.0, dia.totalDia, 0.01)
    }
}

