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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.edusnack.ui.components.CanteenBottomNavBar
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GreenPrimary

// Modelo de dados simples para os itens
data class MenuItem(val id: Int, val name: String, val price: String, val imageUrl: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMenuScreen(nav: NavController) {
    // Mock de dados (Simulando itens do banco de dados)
    val menuItems = remember {
        listOf(
            MenuItem(1, "Sanduíche de frango", "R$ 5,50", "https://example.com/sanduiche.jpg"),
            MenuItem(2, "Salada de frutas", "R$ 4,00", "https://example.com/salada.jpg"),
            MenuItem(3, "Suco de laranja", "R$ 3,00", "https://example.com/suco.jpg"),
            MenuItem(4, "Biscoito integral", "R$ 2,50", "https://example.com/biscoito.jpg")
        )
    }

    // Estado da aba selecionada (0, 1 ou 2)
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Cardápio do dia", "Cardápio semanal", "Histórico")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Gerenciar cardápio",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DarkText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = DarkText
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = { CanteenBottomNavBar(nav) },
        containerColor = Color.White // Garante fundo branco na tela
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- ABAS SUPERIORES (TABS) ---
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = GreenPrimary,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GreenPrimary // Linha verde indicando a aba ativa
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
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        },
                        selectedContentColor = Color(0xFF4CAF50), // Verde quando selecionado
                        unselectedContentColor = Color.Gray // Cinza quando não selecionado
                    )
                }
            }

            // --- CONTEÚDO PRINCIPAL (Lista + Botão Fixo) ---
            Box(modifier = Modifier.weight(1f)) {

                // Lista de Itens
                LazyColumn(
                    // Padding extra no bottom (100.dp) para o último item não ficar escondido atrás do botão
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp, start = 24.dp, end = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(menuItems) { item ->
                        MenuItemRow(item = item, onEditClick = { /* Lógica de editar aqui */ })
                    }
                }

                // Botão "Adicionar item" (Fixo na parte inferior)
                Button(
                    onClick = { nav.navigate("create_menu") }, // Navega para a tela de criar item
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = DarkText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Adicionar item ao cardápio",
                        color = DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// --- COMPONENTE VISUAL DE CADA ITEM DA LISTA ---
@Composable
fun MenuItemRow(item: MenuItem, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Placeholder/Imagem do item
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0)) // Fundo cinza claro
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = item.imageUrl),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Textos (Nome e Preço)
            Column {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = DarkText
                )
                Text(
                    text = item.price,
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50), // Verde
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Ícone de lápis vazado para edição
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Editar item",
                tint = DarkText
            )
        }
    }
}