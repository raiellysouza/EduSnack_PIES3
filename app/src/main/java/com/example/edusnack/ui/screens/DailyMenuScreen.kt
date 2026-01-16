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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.edusnack.model.Cardapio
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.viewmodel.CardapioViewModel

// Cores extraídas da imagem
val GreenText = Color(0xFF4CAF50)
val LightGrayDivider = Color(0xFFEEEEEE)

data class MenuItemData(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val calories: Int,
    val allergens: String,
    val tag: String? = null
)

@Composable
fun DailyMenuScreen(nav: NavController, vm: CardapioViewModel = viewModel()) {
    val tabs = listOf("Salgados", "Bebidas", "Bolos", "Lanches")
    var selectedTab by remember { mutableIntStateOf(0) } 
    val itens by vm.itens.collectAsState()

    val categoriaSelecionada = tabs[selectedTab]

    val filtrados = remember(itens, categoriaSelecionada) {
        itens
            .filter { it.ativo }
            .filter { it.categoria.equals(categoriaSelecionada, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 16.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Menu Diário",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabs.forEachIndexed { index, title ->
                        MenuTabItem(title, selected = selectedTab == index) { 
                            selectedTab = index 
                        }
                    }
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
                .background(Color.White)
                .padding(horizontal = 24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Text(
                    text = categoriaSelecionada,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // "loading" simples: ainda não veio nada
            if (itens.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Carregando menu...", color = GreenText, fontSize = 14.sp)
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                return@LazyColumn
            }

            if (filtrados.isEmpty()) {
                item {
                    Text(
                        text = "Nenhum item cadastrado para $categoriaSelecionada.",
                        color = GreenText,
                        fontSize = 14.sp
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                return@LazyColumn
            }

            items(filtrados.size) { idx ->
                val item = filtrados[idx]
                val ui = item.toMenuItemData()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { nav.navigate("detalhes/${ui.id}") }
                ) {
                    FullDetailCard(data = ui)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

private fun Cardapio.toMenuItemData(): MenuItemData {
    val allergens = buildList {
        if (possuiLactose) add("Laticínios")
        if (possuiGluten) add("Glúten")
    }.let { if (it.isEmpty()) "Nenhum" else it.joinToString(", ") }

    val tag = when {
        vegano -> "Vegano"
        vegetariano -> "Vegetariano"
        else -> null
    }

    return MenuItemData(
        id = id,
        title = nome,
        description = descricao,
        price = preco ?: 0.0,
        imageUrl = imagemUrl ?: "",
        calories = calorias ?: 0,
        allergens = allergens,
        tag = tag
    )
}

@Composable
fun FullDetailCard(data: MenuItemData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                if (data.tag != null) {
                    Text(
                        text = data.tag,
                        color = GreenText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Text(
                    text = data.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = data.description,
                    color = GreenText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            AsyncImage(
                model = data.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .height(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0E0))
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            thickness = 1.dp,
            color = LightGrayDivider
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(0.4f)) {
                Text("Calorias", color = GreenText, fontSize = 12.sp)
                Text("${data.calories}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Column(modifier = Modifier.weight(0.6f)) {
                Text("Alérgenos", color = GreenText, fontSize = 12.sp)
                Text(data.allergens, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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

@Preview(showBackground = true)
@Composable
fun DailyMenuPreview() {
    DailyMenuScreen(nav = rememberNavController())
}
