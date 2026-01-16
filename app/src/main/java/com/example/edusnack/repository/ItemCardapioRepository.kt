package com.example.edusnack.repository

import com.example.edusnack.model.ItemCardapio
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ItemCardapioRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val itens get() = firestore.collection("itens_cardapio")

    // Criar item com validação
    suspend fun criarItem(item: ItemCardapio): Result<String> = try {
        validarItem(item)
        val doc = itens.add(item).await()
        Result.success(doc.id)
    } catch (e: IllegalArgumentException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Atualizar item do cardápio
    suspend fun atualizarItem(item: ItemCardapio): Result<Unit> = try {
        require(item.id.isNotBlank()) { "ID vazio" }
        validarItem(item)
        itens.document(item.id).set(item).await()
        Result.success(Unit)
    } catch (e: IllegalArgumentException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Remover item
    suspend fun removerItem(id: String): Result<Unit> = try {
        require(id.isNotBlank()) { "ID vazio" }
        itens.document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Listar todo o cardapio
    suspend fun listarItens(): Result<List<ItemCardapio>> = try {
        val snapshot = itens.get().await()
        Result.success(snapshot.toObjects(ItemCardapio::class.java))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Listar por categoria
    suspend fun listarPorCategoria(categoria: String): Result<List<ItemCardapio>> = try {
        require(categoria.isNotBlank()) { "Categoria vazia" }
        val snapshot = itens.whereEqualTo("categoria", categoria).get().await()
        Result.success(snapshot.toObjects(ItemCardapio::class.java))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Buscar item por ID
    suspend fun buscarPorId(id: String): Result<ItemCardapio?> = try {
        require(id.isNotBlank()) { "ID vazio" }
        val doc = itens.document(id).get().await()
        Result.success(doc.toObject(ItemCardapio::class.java))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Buscar por faixa de preço
    suspend fun listarPorFaixaPreco(min: Double, max: Double): Result<List<ItemCardapio>> = try {
        require(min >= 0) { "Preço mínimo inválido" }
        require(max >= min) { "Preço máximo inválido" }

        val snapshot = itens
            .whereGreaterThanOrEqualTo("preco", min)
            .whereLessThanOrEqualTo("preco", max)
            .orderBy("preco")
            .get()
            .await()

        Result.success(snapshot.toObjects(ItemCardapio::class.java))

    } catch (e: Exception) {
        Result.failure(e)
    }

    // Itens disponíveis
    suspend fun listarItensDisponiveis(): Result<List<ItemCardapio>> = try {
        val snapshot = itens
            .whereEqualTo("disponivel", true)
            .get()
            .await()

        Result.success(snapshot.toObjects(ItemCardapio::class.java))

    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun listarDisponiveisHoje(diaSemana: String): Result<List<ItemCardapio>> = try {
        val snapshot = itens
            .whereEqualTo("disponivel", true)
            .whereArrayContains("diasDisponiveis", diaSemana) // ex: "MONDAY"
            .get()
            .await()

        Result.success(snapshot.toObjects(ItemCardapio::class.java))
    } catch (e: Exception) {
        Result.failure(e)
    }


    // Validação completa
    private fun validarItem(item: ItemCardapio) {
        require(item.nome.isNotBlank()) { "Nome obrigatório" }
        require(item.nome.length >= 3) { "Nome muito curto" }

        require(item.categoria.isNotBlank()) { "Categoria obrigatória" }
        require(item.preco > 0) { "Preço deve ser maior que zero" }

        require(item.descricao.isNotBlank()) { "Descrição obrigatória" }
        require(item.descricao.length >= 5) { "Descrição muito curta" }

        require(item.calorias >= 0) { "Calorias inválidas" }
        require(item.alergenicos.all { it.isNotBlank() }) { "Alergênicos inválidos" }

        if (item.imagemUrl.isNotBlank()) {
            require(item.imagemUrl.length >= 5) { "URL de imagem inválida" }
        }
    }
}
