package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.viewmodel.CardapioViewModel

@Composable
fun ItemDetailsScreen(
    nav: NavController,
    itemId: String,
    vm: CardapioViewModel = viewModel()
) {
    val itemSelecionado by vm.itemSelecionado.collectAsState()

    LaunchedEffect(itemId) {
        vm.carregarItem(itemId)
    }

    itemSelecionado?.let { item ->

        Scaffold(
            bottomBar = { BottomNavBar(nav) }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {

                Spacer(Modifier.height(16.dp))

                Text("Detalhes do Item", style = MaterialTheme.typography.headlineSmall)

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .height(240.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFDADADA), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!item.imagemUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = item.imagemUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("Sem imagem")
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(item.nome, style = MaterialTheme.typography.headlineMedium)

                Spacer(Modifier.height(8.dp))

                Text(item.descricao, color = Color.Gray)

                Spacer(Modifier.height(24.dp))

                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Preço", color = Color(0xFF4CAF50))
                        Text("R$ %.2f".format(item.preco))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Categoria", color = Color(0xFF4CAF50))
                        Text(item.categoria)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text("Restrições", color = Color(0xFF4CAF50))

                val restricoes = buildList {
                    if (item.possuiLactose) add("Contém lactose")
                    if (item.possuiGluten) add("Contém glúten")
                    if (item.vegano) add("Vegano")
                    if (item.vegetariano) add("Vegetariano")
                }.ifEmpty { listOf("Nenhuma restrição marcada") }

                Column {
                    restricoes.forEach { Text("• $it") }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = { nav.navigate("carrinho/add/${item.id}") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Adicionar ao Carrinho")
                }
            }
        }
    }
}
