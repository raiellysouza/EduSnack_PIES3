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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.ui.components.HighlightItemCard

@Composable
fun AdvanceOrderScreen(nav: NavController) {
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

            // ITEM 1: Salada Caesar
            item {
                // Envolvemos o card em um Box clicável
                Box(
                    modifier = Modifier.clickable {
                        // Navega para a tela de detalhes (use o ID se configurou rota dinâmica)
                        nav.navigate("detalhes/1")
                    }
                ) {
                    HighlightItemCard(
                        tag = "Popular",
                        title = "Salada Caesar de Frango",
                        description = "Alface romana crocante, frango grelhado, queijo parmesão e croutons com molho Caesar.",
                        price = 8.99,
                        imageUrl = "https://example.com/caesar.jpg",
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ITEM 2: Sanduíche
            item {
                Box(
                    modifier = Modifier.clickable {
                        nav.navigate("detalhes/2")
                    }
                ) {
                    HighlightItemCard(
                        tag = "Novo",
                        title = "Sanduíche de Peru e Suíço",
                        description = "Peito de peru fatiado, queijo suíço, alface, tomate e maionese no pão integral.",
                        price = 6.49,
                        imageUrl = "https://example.com/peru.jpg"
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ITEM 3: Massa
            item {
                Box(
                    modifier = Modifier.clickable {
                        nav.navigate("detalhes/3")
                    }
                ) {
                    HighlightItemCard(
                        tag = "Vegetariano",
                        title = "Massa Primavera",
                        description = "Massa penne com uma mistura de vegetais frescos em um molho leve de alho e ervas.",
                        price = 7.99,
                        imageUrl = "https://example.com/massa_primavera.jpg"
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