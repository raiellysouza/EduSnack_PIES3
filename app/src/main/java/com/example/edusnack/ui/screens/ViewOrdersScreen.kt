package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edusnack.model.Pedido
import com.example.edusnack.model.StatusPedido
import com.example.edusnack.ui.components.CanteenBottomNavBar
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GreenPrimary
import com.example.edusnack.viewmodel.PedidoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrdersScreen(nav: NavController, ordersViewModel: PedidoViewModel = viewModel()) {
    // Estados para os filtros
    var selectedTimeTab by remember { mutableStateOf(0) } // 0: Hoje, 1: Semana, 2: Todos
    val timeTabs = listOf("Hoje", "Semana", "Todos")

    var selectedStatusTab by remember { mutableStateOf(0) } // 0: Pendentes, 1: Preparando...
    val statusTabs = listOf("Pendentes", "Preparando", "Prontos", "Entregues")

    var searchText by remember { mutableStateOf("") }

    val ordersBySeries by ordersViewModel.ordersBySeries.collectAsState()
    val loading by ordersViewModel.loading.collectAsState()
    val error by ordersViewModel.error.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Pedidos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
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
            // --- ABAS DE TEMPO (Hoje / Semana / Todos) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                timeTabs.forEachIndexed { index, title ->
                    Column(
                        modifier = Modifier
                            .padding(end = 24.dp)
                            .clickable { selectedTimeTab = index },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            color = if (selectedTimeTab == index) DarkText else Color.Gray,
                            fontWeight = if (selectedTimeTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
                        )
                        if (selectedTimeTab == index) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(3.dp)
                                    .background(GreenPrimary, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ABAS DE STATUS (Pendentes / Preparando...) ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                statusTabs.forEach { status ->
                    val isSelected = status == statusTabs[selectedStatusTab]
                    Text(
                        text = status,
                        color = if (isSelected) GreenPrimary else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            selectedStatusTab = statusTabs.indexOf(status)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BARRA DE BUSCA ---
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar aluno", color = Color(0xFF4CAF50)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF4CAF50)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE9F2E8),
                        unfocusedContainerColor = Color(0xFFE9F2E8),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- LISTA DE PEDIDOS AGRUPADOS POR TURMA ---
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Erro: ${error}", color = Color.Red)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Iterate over series and render header + list or empty message
                    ordersBySeries.forEach { (series, pedidos) ->
                        // Header for the series
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(series, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        if (pedidos.isEmpty()) {
                            item {
                                Text("Sem pedidos dessa turma", color = Color.Gray, modifier = Modifier.padding(start = 24.dp))
                            }
                        } else {
                            items(pedidos) { pedido ->
                                SimpleOrderCard(pedido = pedido, onMarkDelivered = {
                                    ordersViewModel.markAsDelivered(pedido.id)
                                })
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
fun SimpleOrderCard(pedido: Pedido, onMarkDelivered: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF8FFF8), RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = pedido.alunoNome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)
                val itemsText = pedido.itens.joinToString(", ") { it.nome }
                Text(text = itemsText, color = Color.Gray, fontSize = 14.sp)
                Text(text = "Código: ${pedido.codigoRetirada}", color = Color.Gray, fontSize = 12.sp)
            }
            Text(text = "R$ ${String.format("%.2f", pedido.total)}", fontWeight = FontWeight.Bold, color = DarkText)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onMarkDelivered, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
                Text("Marcar entregue", color = Color.White)
            }
        }
    }
}
