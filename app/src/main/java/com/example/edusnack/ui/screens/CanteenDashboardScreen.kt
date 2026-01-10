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
import com.example.edusnack.ui.components.CanteenBottomNavBar
import com.example.edusnack.ui.components.SummaryDashboardCard
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GrayBackground
import com.example.edusnack.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteenDashboardScreen(
    nav: NavController,
    // Data-driven parameters (default to neutral values until ViewModel provides real data)
    activeMenusCount: Int = 0,
    pendingOrdersCount: Int = 0,
    readyOrdersCount: Int = 0,
    totalSales: Double = 0.0,
    // Callbacks for actions (keep composable stateless)
    onCreateMenu: () -> Unit = { nav.navigate("create_menu") },
    onViewOrders: () -> Unit = { nav.navigate("view_orders") },
    onSettings: () -> Unit = { nav.navigate("settings") },
    onNotifications: () -> Unit = { nav.navigate("notifications") },
    onLogout: () -> Unit = {
        // Default behaviour: sign out handled elsewhere, navigate to login
        nav.navigate("login") { popUpTo("welcome") { inclusive = true } }
    }
) {
    var overflowExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = GrayBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Painel da Cantina",
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GrayBackground
                ),
                actions = {
                    // Overflow menu (three-dots)
                    IconButton(onClick = { overflowExpanded = true }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Mais")
                    }

                    DropdownMenu(
                        expanded = overflowExpanded,
                        onDismissRequest = { overflowExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Configurações") },
                            onClick = {
                                overflowExpanded = false
                                onSettings()
                            },
                            leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Notificações") },
                            onClick = {
                                overflowExpanded = false
                                onNotifications()
                            },
                            leadingIcon = { Icon(Icons.Filled.Notifications, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                overflowExpanded = false
                                onLogout()
                            },
                            leadingIcon = { Icon(Icons.Filled.Logout, contentDescription = null) }
                        )
                    }
                }
            )
        },
        // Removed unsupported 'selected' parameter — BottomNav manages its own selection internally
        bottomBar = { CanteenBottomNavBar(nav) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            // --- Título da Seção Resumo ---
            item {
                Text(
                    text = "Resumo do Dia",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // --- CARDS DE RESUMO ---
            item {
                SummaryDashboardCard(
                    title = "Cardápios ativos hoje",
                    value = if (activeMenusCount > 0) activeMenusCount.toString() else "0",
                    description = if (activeMenusCount > 0) "Gerencie os cardápios ativos para hoje" else "Sem dados para hoje",
                    imageUrl = ""
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SummaryDashboardCard(
                    title = "Pedidos pendentes",
                    value = if (pendingOrdersCount > 0) pendingOrdersCount.toString() else "0",
                    description = if (pendingOrdersCount > 0) "Pedidos aguardando preparo" else "Sem dados para hoje",
                    imageUrl = ""
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SummaryDashboardCard(
                    title = "Pedidos prontos para retirada",
                    value = if (readyOrdersCount > 0) readyOrdersCount.toString() else "0",
                    description = if (readyOrdersCount > 0) "Prontos para retirada" else "Sem dados para hoje",
                    imageUrl = ""
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SummaryDashboardCard(
                    title = "Total de vendas do dia",
                    value = if (totalSales > 0.0) String.format("R$ %.2f", totalSales) else "R$ 0,00",
                    description = if (totalSales > 0.0) "Vendas totais de hoje" else "Sem dados para hoje",
                    imageUrl = ""
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- Título da Seção Ações ---
            item {
                Text(
                    text = "Ações",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // --- PRIMARY ACTION + Secondary ---
            item {
                // Primary: Criar cardápio
                ActionButton(
                    text = "Criar cardápio",
                    backgroundColor = GreenPrimary,
                    textColor = Color.White,
                    onClick = onCreateMenu
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                // Secondary: Ver pedidos
                ActionButton(
                    text = "Ver pedidos",
                    backgroundColor = Color(0xFFE8F5E9),
                    textColor = DarkText,
                    onClick = onViewOrders
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Componente local auxiliar para os botões ficarem padronizados
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