package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.navigation.NavController
import com.example.edusnack.ui.components.CanteenBottomNavBar
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrdersScreen(nav: NavController) {
    // Estados para os filtros
    var selectedTimeTab by remember { mutableStateOf(0) } // 0: Hoje, 1: Semana, 2: Todos
    val timeTabs = listOf("Hoje", "Semana", "Todos")

    var selectedStatusTab by remember { mutableStateOf(0) } // 0: Pendentes, 1: Preparando...
    val statusTabs = listOf("Pendentes", "Preparando", "Prontos", "Entregues")

    var searchText by remember { mutableStateOf("") }

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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(statusTabs) { status ->
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

            // --- LISTA DE PEDIDOS ---
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Mock de Pedidos
                item {
                    OrderCard(
                        studentName = "Lucas Silva / 6ºB",
                        time = "12:30",
                        items = "Sanduíche de Frango, Suco de Laranja",
                        price = "R$ 15,00",
                        status = "Preparando"
                    )
                }

                item {
                    OrderCard(
                        studentName = "Sofia Mendes / 7ºA",
                        time = "13:00",
                        items = "Salada de Frutas, Água",
                        price = "R$ 12,00",
                        status = "Pronto"
                    )
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun OrderCard(
    studentName: String,
    time: String,
    items: String,
    price: String,
    status: String // "Pendente", "Preparando", "Pronto"
) {
    var idRetirada by remember { mutableStateOf("") }
    var currentStatus by remember { mutableStateOf(status) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Cabeçalho do Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = studentName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DarkText
                )
                Text(
                    text = "Agendado para $time",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = items,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = price,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DarkText
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botões de Status (Toggle)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val statuses = listOf("Entregue", "Preparando", "Pronto")
            statuses.forEach { stat ->
                 StatusToggleItem(stat, currentStatus == stat) {
                     currentStatus = stat
                 }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Campo ID de Retirada
        OutlinedTextField(
            value = idRetirada,
            onValueChange = { idRetirada = it },
            placeholder = { Text("ID de Retirada", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = GreenPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botões de Ação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { /* Marcar entregue */ },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Marcar como entregue", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { /* Cancelar */ },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar pedido", color = DarkText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatusToggleItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) Color.White else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) GreenPrimary else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
