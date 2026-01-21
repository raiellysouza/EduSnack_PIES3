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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Logout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edusnack.ui.components.CanteenBottomNavBar
import com.example.edusnack.ui.components.SummaryDashboardCard
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GrayBackground
import com.example.edusnack.ui.theme.GreenPrimary
import com.example.edusnack.viewmodel.CanteenDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteenDashboardScreen(
    nav: NavController,
    vm: CanteenDashboardViewModel = viewModel()
) {
    val activeMenusCount by vm.activeMenusCount.collectAsState()
    val pendingOrdersCount by vm.pendingOrdersCount.collectAsState()
    val readyOrdersCount by vm.readyOrdersCount.collectAsState()
    val totalSales by vm.totalSales.collectAsState()
    val loading by vm.loading.collectAsState()

    var overflowExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Painel da Cantina",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = { overflowExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert, 
                            contentDescription = "Mais",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = overflowExpanded,
                        onDismissRequest = { overflowExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Configurações") },
                            onClick = {
                                overflowExpanded = false
                                nav.navigate("settings")
                            },
                            leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Notificações") },
                            onClick = {
                                overflowExpanded = false
                                nav.navigate("notifications")
                            },
                            leadingIcon = { Icon(Icons.Filled.Notifications, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                overflowExpanded = false
                                nav.navigate("login") { popUpTo("welcome") { inclusive = true } }
                            },
                            leadingIcon = { Icon(Icons.Filled.Logout, contentDescription = null) }
                        )
                    }
                }
            )
        },
        bottomBar = { CanteenBottomNavBar(nav) }
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp)
            ) {
                item {
                    Text(
                        text = "Resumo do Dia",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                item {
                    SummaryDashboardCard(
                        title = "Cardápios ativos hoje",
                        value = activeMenusCount.toString(),
                        description = if (activeMenusCount > 0) "Gerencie os cardápios ativos para hoje" else "Sem cardápios ativos",
                        imageUrl = ""
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    SummaryDashboardCard(
                        title = "Pedidos pendentes",
                        value = pendingOrdersCount.toString(),
                        description = if (pendingOrdersCount > 0) "Pedidos aguardando preparo" else "Nenhum pedido pendente",
                        imageUrl = ""
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    SummaryDashboardCard(
                        title = "Pedidos prontos para retirada",
                        value = readyOrdersCount.toString(),
                        description = if (readyOrdersCount > 0) "Prontos para retirada" else "Nenhum pedido pronto",
                        imageUrl = ""
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    SummaryDashboardCard(
                        title = "Total de vendas do dia",
                        value = String.format("R$ %.2f", totalSales),
                        description = "Vendas acumuladas hoje",
                        imageUrl = ""
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }

                item {
                    Text(
                        text = "Ações",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    ActionButton(
                        text = "Criar cardápio",
                        backgroundColor = GreenPrimary,
                        textColor = Color.Black,
                        onClick = { nav.navigate("create_menu") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    ActionButton(
                        text = "Ver pedidos",
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = { nav.navigate("view_orders") }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
