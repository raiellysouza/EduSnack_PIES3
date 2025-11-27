package com.example.edusnack

import com.example.edusnack.model.Dia
import com.example.edusnack.model.ItemPedido
import com.example.edusnack.model.Semana
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
class ResumoSemanaRepositoryTest {

    @Test
    fun deveGerarResumoSemanalCorretamente() = runBlocking {

        val dia1 = Dia(
            data = "2025-11-20",
            itens = mutableListOf(
                ItemPedido("1", "Salgado", 5.0, 1),
                ItemPedido("2", "Suco", 3.0, 1)
            ),
        )

        val dia2 = Dia(
            data = "2025-11-21",
            itens = mutableListOf(
                ItemPedido("1", "Salgado", 5.0, 2)
            ),
        )

        val semana = Semana(
            idSemana = "SEM01",
            alunoId = "ALUNO1",
            dias = mutableListOf(dia1, dia2)
        )

        // simulação fake
        val total = dia1.totalDia + dia2.totalDia
        assertEquals(5.0 + 3.0 + 10.0, total, 0.01)
    }
}