package com.example.edusnack.ui.screens

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
import coil.compose.AsyncImage
import com.example.edusnack.model.Cardapio
import com.example.edusnack.ui.components.CanteenBottomNavBar
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GreenPrimary
import com.example.edusnack.viewmodel.CardapioViewModel
import com.example.edusnack.viewmodel.HistoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMenuScreen(nav: NavController, vm: CardapioViewModel = viewModel()) {
    val menuItems by vm.itens.collectAsState()
    val historyItems by vm.historyItems.collectAsState()
    val loading by vm.loading.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Cardápio", "Histórico")

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
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = DarkText
                        )
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
                        selectedContentColor = GreenPrimary,
                        unselectedContentColor = Color.Gray
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = GreenPrimary
                    )
                } else {
                    // TAB 0 = CARDÁPIO FIXO
                    if (selectedTab == 0) {
                        if (menuItems.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Nenhum item no cardápio.", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(
                                    top = 16.dp,
                                    bottom = 100.dp,
                                    start = 24.dp,
                                    end = 24.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(menuItems) { item ->
                                    MenuItemRow(item = item, onEditClick = { /* Editar */ })
                                }
                            }
                        }

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
                            Text(
                                "Adicionar item ao cardápio",
                                color = DarkText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                    // TAB 1 = HISTÓRICO
                    else {
                        Column {
                            Row(modifier = Modifier.padding(16.dp)) {
                                FilterChip(label = "Data")
                                Spacer(modifier = Modifier.width(12.dp))
                                FilterChip(label = "Tipo de alteração")
                            }

                            if (historyItems.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Nenhum histórico encontrado.", color = Color.Gray)
                                }
                            } else {
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
                    }
                }
            }
        }
    }
}

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
                if (!item.imagemUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = item.imagemUrl,
                        contentDescription = item.nome,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("🍽️", fontSize = 24.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(item.nome, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = DarkText)
                Text(
                    "R$ ${String.format("%.2f", item.preco ?: 0.0)}",
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        IconButton(onClick = onEditClick) {
            Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = DarkText)
        }
    }
}

@Composable
fun HistoryItemRow(item: HistoryItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0))
            ) {
                AsyncImage(
                    model = item.imageUrl,
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
                    color = Color(0xFF4CAF50),
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

        Text(
            text = item.date,
            fontSize = 12.sp,
            color = Color(0xFF4CAF50),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun FilterChip(label: String) {
    Surface(
        color = Color(0xFFE9F2E8),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(label, fontSize = 13.sp, color = DarkText)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = DarkText
            )
        }
    }
}
