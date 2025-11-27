package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.edusnack.model.Cardapio
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.viewmodel.CardapioViewModel

@Composable
fun HomeScreen(nav: NavController, vm: CardapioViewModel = viewModel()) {

    val itens by vm.itens.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {

            Spacer(Modifier.height(20.dp))

            Text("Pedido Antecipado", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(24.dp))

            Text("Cardápio", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            LazyRow {
                items(3) { index ->
                    CategoryCard(
                        nome = when (index) {
                            0 -> "Saladas"
                            1 -> "Sanduíches"
                            else -> "Massas"
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Itens em Destaque", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(itens.size) { i ->
                    ItemCard(item = itens[i]) {
                        nav.navigate("detalhes/${itens[i].id}")
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryCard(nome: String) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(60.dp)
            .background(Color(0xFFE9E9E9), RoundedCornerShape(12.dp))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Text(nome)
    }
}

@Composable
fun ItemCard(item: Cardapio, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.nome, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(item.descricao, color = Color.Gray, maxLines = 2)
            Spacer(Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .background(Color(0xFFE9F2E8), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text("R$ %.2f".format(item.preco))
            }
        }

        Spacer(Modifier.width(20.dp))

        Box(
            modifier = Modifier
                .size(110.dp)
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
                Text("Sem\nimagem")
            }
        }
    }
}
