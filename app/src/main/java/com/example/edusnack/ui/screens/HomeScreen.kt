package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.edusnack.model.Cardapio
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.viewmodel.CardapioViewModel
import com.example.edusnack.viewmodel.CarrinhoViewModel

@Composable
fun HomeScreen(nav: NavController, cardapioVm: CardapioViewModel, carrinhoVm: CarrinhoViewModel) {
    val itens by cardapioVm.itens.collectAsState()
    val categorias by cardapioVm.categorias.collectAsState()
    
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val itensExibidos = remember(itens, selectedCategory) {
        if (selectedCategory == null) itens else itens.filter { it.categoria == selectedCategory }
    }

    Scaffold(bottomBar = { BottomNavBar(nav) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(20.dp))
            Text("Pedido Antecipado", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            
            Spacer(Modifier.height(24.dp))
            Text("Categorias", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    CategoryCard(
                        nome = "Todos", 
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                items(categorias) { categoria ->
                    CategoryCard(
                        nome = categoria, 
                        selected = selectedCategory == categoria,
                        onClick = { selectedCategory = categoria }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = if (selectedCategory == null) "Itens em Destaque" else "Itens em $selectedCategory", 
                style = MaterialTheme.typography.titleLarge, 
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            
            if (itensExibidos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum item encontrado", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    items(itensExibidos) { item ->
                        ItemCard(item = item) { nav.navigate("detalhes/${item.id}") }
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(nome: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(60.dp)
            .background(
                if (selected) Color(0xFF4CAF50) else Color(0xFFF5F5F5), 
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }, 
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = nome, 
            color = if (selected) Color.White else Color.Black,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun ItemCard(item: Cardapio, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(item.descricao, color = Color.Gray, maxLines = 2, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.background(Color(0xFFE9F2E8), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text(
                    text = "R$ %.2f".format(item.preco ?: 0.0).replace('.', ','),
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.width(20.dp))
        Box(modifier = Modifier.size(100.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            if (!item.imagemUrl.isNullOrBlank()) {
                AsyncImage(model = item.imagemUrl, contentDescription = null, modifier = Modifier.fillMaxSize().background(Color.White, RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            } else {
                Text("🍽️", fontSize = 32.sp)
            }
        }
    }
}
