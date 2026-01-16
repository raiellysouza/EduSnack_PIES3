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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edusnack.model.Pedido
import com.example.edusnack.ui.components.CanteenBottomNavBar
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GreenPrimary
import com.example.edusnack.viewmodel.PedidoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrdersScreen(nav: NavController, viewModel: PedidoViewModel = viewModel()) {
    // Estados para os filtros
    var selectedDayFilter by remember { mutableStateOf("Todos") }
    val diasFiltro = listOf("Todos", "Seg", "Ter", "Qua", "Qui", "Sex")

    var selectedStatusTab by remember { mutableIntStateOf(0) }
    val statusTabs = listOf("Pendentes", "Preparando", "Prontos", "Entregues")

    var searchText by remember { mutableStateOf("") }

    val ordersBySeries by viewModel.ordersBySeries.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pedidos", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
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
            // --- FILTRO POR DIA DA SEMANA ---
            Text(
                text = "Filtrar por dia da semana:",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                diasFiltro.forEach { dia ->
                    val isSelected = selectedDayFilter == dia
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GreenPrimary else Color(0xFFF5F5F5))
                            .clickable { selectedDayFilter = dia },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dia,
                            color = if (isSelected) Color.White else Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ABAS DE STATUS ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                statusTabs.forEachIndexed { index, status ->
                    val isSelected = index == selectedStatusTab
                    Text(
                        text = status,
                        color = if (isSelected) GreenPrimary else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { selectedStatusTab = index }
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

            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error ?: "Erro desconhecido", color = Color.Red)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ordersBySeries.forEach { (series, pedidos) ->
                        // Filtrar pedidos pelo dia selecionado
                        val pedidosFiltrados = pedidos.filter { pedido ->
                            if (selectedDayFilter == "Todos") true
                            else pedido.itens.any { item -> item.diasReserva.contains(selectedDayFilter) }
                        }

                        if (pedidosFiltrados.isNotEmpty()) {
                            item {
                                Text(series, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)
                            }
                            items(pedidosFiltrados) { pedido ->
                                OrderCardWithDays(pedido = pedido, onMarkDelivered = {
                                    viewModel.markAsDelivered(pedido.id)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCardWithDays(pedido: Pedido, onMarkDelivered: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF8FFF8), RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = pedido.alunoNome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)
                
                // Exibe itens e seus respectivos dias
                pedido.itens.forEach { item ->
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Text(text = "• ${item.nome}", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        if (item.diasReserva.isNotEmpty()) {
                            Text(
                                text = "Dias: ${item.diasReserva.joinToString(", ")}",
                                color = GreenPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
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
