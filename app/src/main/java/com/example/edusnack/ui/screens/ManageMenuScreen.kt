package com.example.edusnack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.edusnack.model.Cardapio
import com.example.edusnack.ui.components.CanteenBottomNavBar
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GreenPrimary
import com.example.edusnack.viewmodel.CardapioViewModel


// Modelo para Itens do Histórico (Aba 2) - Novo!
data class HistoryItem(
    val id: Int,
    val name: String,
    val actionText: String, // Ex: "Adicionado por Ana Silva"
    val date: String,       // Ex: "15/07/2024"
    val price: String,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMenuScreen(nav: NavController, vm: CardapioViewModel = viewModel()) {
    val menuItems by vm.itens.collectAsState()

    // Mock específico para o Histórico conforme sua imagem
    val historyItems = remember {
        listOf(
            HistoryItem(1, "Sanduíche de frango", "Adicionado por Ana Silva", "15/07/2024", "R$ 5,50", "https://example.com/sanduiche.jpg"),
            HistoryItem(2, "Salada de frutas", "Removido por Carlos Mendes", "10/07/2024", "R$ 4,00", "https://example.com/salada.jpg"),
            HistoryItem(3, "Suco de laranja", "Preço alterado por Joana Pereira", "05/07/2024", "R$ 3,50", "https://example.com/suco.jpg"),
            HistoryItem(4, "Biscoito integral", "Adicionado por Pedro Almeida", "01/07/2024", "R$ 2,50", "https://example.com/biscoito.jpg")
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Cardápio do dia", "Cardápio semanal", "Histórico")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Gerenciar cardápio", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkText)
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = DarkText)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { CanteenBottomNavBar(nav) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- ABAS ---
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = GreenPrimary,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GreenPrimary
                        )
                    }
                },
                divider = { HorizontalDivider(color = Color(0xFFF0F0F0)) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        },
                        selectedContentColor = Color(0xFF4CAF50),
                        unselectedContentColor = Color.Gray
                    )
                }
            }

            // --- CONTEÚDO ---
            Box(modifier = Modifier.weight(1f)) {

                // Se for Histórico (Aba 2), mostra o layout novo
                if (selectedTab == 2) {
                    Column {
                        // Filtros (Data / Tipo de alteração)
                        Row(modifier = Modifier.padding(16.dp)) {
                            FilterChip(label = "Data")
                            Spacer(modifier = Modifier.width(12.dp))
                            FilterChip(label = "Tipo de alteração")
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(historyItems) { item ->
                                HistoryItemRow(item)
                            }
                        }
                    }
                }
                // Se for Cardápio (Aba 0 ou 1), mostra o layout padrão com botão de adicionar
                else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp, start = 24.dp, end = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(menuItems) { item ->
                            MenuItemRow(item = item, onEditClick = { /* Editar */ })
                        }
                    }

                    // Botão Adicionar (Apenas nas abas de cardápio)
                    Button(
                        onClick = { nav.navigate("create_menu") },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(24.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = DarkText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adicionar item ao cardápio", color = DarkText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// --- CARD PADRÃO (Para Cardápio) ---
@Composable
fun MenuItemRow(item: Cardapio, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = ""),
                    contentDescription = item.nome,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(item.nome, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = DarkText)
                Text("R$ ${item.preco}", fontSize = 14.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
            }
        }
        IconButton(onClick = onEditClick) {
            Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = DarkText)
        }
    }
}

// --- CARD DE HISTÓRICO (Para Aba Histórico) ---
@Composable
fun HistoryItemRow(item: HistoryItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f)) {
            // Imagem um pouco maior no histórico conforme imagem
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF0F0F0))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = item.imageUrl),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = DarkText
                )
                Text(
                    text = item.actionText,
                    fontSize = 13.sp,
                    color = Color(0xFF4CAF50), // Verde conforme protótipo
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.price,
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Data no canto direito
        Text(
            text = item.date,
            fontSize = 12.sp,
            color = Color(0xFF4CAF50), // Verde
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// --- COMPONENTE DE FILTRO (Botãozinho cinza claro) ---
@Composable
fun FilterChip(label: String) {
    Surface(
        color = Color(0xFFE9F2E8), // Verde bem clarinho
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(label, fontSize = 13.sp, color = DarkText)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = DarkText)
        }
    }
}
