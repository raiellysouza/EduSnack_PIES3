package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.ui.components.HighlightItemCard
import com.example.edusnack.viewmodel.CardapioViewModel

@Composable
fun AdvanceOrderScreen(nav: NavController, vm: CardapioViewModel = viewModel()) {
    val itens by vm.itens.collectAsState()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pedido Antecipado",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(horizontal = 24.dp)
        ) {

            // --- SEÇÃO 1: CARDÁPIO DE HOJE ---
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cardápio de Hoje",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { CategoryItem("Saladas", "https://example.com/salada.jpg") }
                    item { CategoryItem("Sanduíches", "https://example.com/sanduiche.jpg") }
                    item { CategoryItem("Massas", "https://example.com/massa.jpg") }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- SEÇÃO 2: ITENS EM DESTAQUE ---
            item {
                Text(
                    text = "Itens em Destaque",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            items(itens) { item ->
                Box(
                    modifier = Modifier.clickable {
                        nav.navigate("detalhes/${item.id}")
                    }
                ) {
                    HighlightItemCard(
                        tag = if (item.vegano) "Vegano" else "Popular",
                        title = item.nome,
                        description = item.descricao,
                        price = String.format("%.2f", item.preco).replace(".", ","),
                        imageUrl = item.imagemUrl ?: "",
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ... Restante do código (CategoryItem) permanece igual ...
@Composable
fun CategoryItem(name: String, imageUrl: String) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.width(140.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF0F0F0))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Preview
@Composable
fun AdvanceOrderPreview() {
    AdvanceOrderScreen(nav = rememberNavController())
}
