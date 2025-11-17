package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

// Cores extraídas da imagem
val GreenText = Color(0xFF4CAF50)
val DarkGreenText = Color(0xFF2E7D32)
val LightGrayDivider = Color(0xFFEEEEEE)

data class MenuItemData(
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val calories: Int,
    val allergens: String,
    val tag: String? = null // Ex: "Vegetariano"
)

@Composable
fun DailyMenuScreen(nav: NavController) {
    // Estado para controlar a aba selecionada (0, 1 ou 2)
    var selectedTab by remember { mutableIntStateOf(1) } // Começa no "Almoço"

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 16.dp)
            ) {
                // Título Central
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Menu Diário",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Tabs Customizadas (Café, Almoço, Lanches)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MenuTabItem("Café da Manhã", selected = selectedTab == 0) { selectedTab = 0 }
                    MenuTabItem("Almoço", selected = selectedTab == 1) { selectedTab = 1 }
                    MenuTabItem("Lanches", selected = selectedTab == 2) { selectedTab = 2 }
                }
                HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 2.dp)
            }
        },
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White) // Fundo geral branco
                .padding(horizontal = 24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // --- SEÇÃO 1: PRATO PRINCIPAL ---
            item {
                Text(
                    text = "Prato Principal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // ITEM 1: Massa Primavera (Clicável)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { nav.navigate("detalhes/3") } // Redireciona para o ID 3
                ) {
                    FullDetailCard(
                        data = MenuItemData(
                            title = "Massa Primavera",
                            description = "Massa fresca com legumes da época em um molho cremoso leve.",
                            price = 5.99,
                            imageUrl = "https://example.com/pasta.jpg",
                            calories = 450,
                            allergens = "Laticínios, Glúten",
                            tag = "Vegetariano"
                        )
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- SEÇÃO 2: ACOMPANHAMENTOS ---
            item {
                Text(
                    text = "Acompanhamentos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // ITEM 2: Salada (Clicável)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { nav.navigate("detalhes/4") } // Redireciona para o ID 4
                ) {
                    FullDetailCard(
                        data = MenuItemData(
                            title = "Salada de Jardim",
                            description = "Mix de folhas verdes com tomates cereja, pepinos e um molho vinagrete.",
                            price = 2.50,
                            imageUrl = "https://example.com/salad.jpg",
                            calories = 150,
                            allergens = "Nenhum"
                        )
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ITEM 3: Frutas (Clicável)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { nav.navigate("detalhes/5") } // Redireciona para o ID 5
                ) {
                    FullDetailCard(
                        data = MenuItemData(
                            title = "Copo de Frutas",
                            description = "Uma mistura refrescante de frutas da época.",
                            price = 1.75,
                            imageUrl = "https://example.com/fruit.jpg",
                            calories = 100,
                            allergens = "Nenhum"
                        )
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// --- COMPONENTE DO CARD COMPLETO (Inalterado) ---
@Composable
fun FullDetailCard(data: MenuItemData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Coluna de Textos
            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                // Label opcional (Ex: Vegetariano)
                if (data.tag != null) {
                    Text(
                        text = data.tag,
                        color = GreenText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Título
                Text(
                    text = data.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Descrição em Verde
                Text(
                    text = data.description,
                    color = GreenText, // Usando a cor verde específica
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Imagem Arredondada
            AsyncImage(
                model = data.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .height(70.dp) // Formato mais retangular como na imagem
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0E0))
            )
        }

        // Linha Divisória Fina
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            thickness = 1.dp,
            color = LightGrayDivider
        )

        // Informações Nutricionais
        Row(modifier = Modifier.fillMaxWidth()) {
            // Calorias
            Column(modifier = Modifier.weight(0.4f)) {
                Text("Calorias", color = GreenText, fontSize = 12.sp)
                Text("${data.calories}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            // Alérgenos
            Column(modifier = Modifier.weight(0.6f)) {
                Text("Alérgenos", color = GreenText, fontSize = 12.sp)
                Text(data.allergens, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Preço
        Column {
            Text("Preço", color = GreenText, fontSize = 12.sp)
            Text(
                text = "R$ %.2f".format(data.price).replace('.', ','),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

// --- COMPONENTE DA ABA (TAB) ---
@Composable
fun MenuTabItem(text: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = if (selected) Color.Black else GreenText.copy(alpha = 0.7f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // Linha indicadora da aba selecionada
        if (selected) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(3.dp)
                    .background(Color.Black, RoundedCornerShape(2.dp))
            )
        } else {
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}

// Preview para ver como ficou
@Preview(showBackground = true)
@Composable
fun DailyMenuPreview() {
    DailyMenuScreen(nav = rememberNavController())
}