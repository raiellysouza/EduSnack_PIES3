package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.ui.theme.GreenPrimary

// Modelo de dados simples para as transações
data class PurchaseTransaction(
    val title: String,
    val studentName: String,
    val date: String,
    val price: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseStatementScreen(nav: NavController) {
    // Lista de transações baseada na imagem enviada
    val transactions = listOf(
        PurchaseTransaction("Sanduíche de Frango", "Aluno 1", "12/07/2024 14:30", "R$ 8,50"),
        PurchaseTransaction("Suco de Laranja", "Aluno 2", "12/07/2024 12:15", "R$ 4,00"),
        PurchaseTransaction("Biscoito de Chocolate", "Aluno 1", "11/07/2024 15:45", "R$ 3,00"),
        PurchaseTransaction("Salada de Frutas", "Aluno 2", "11/07/2024 11:00", "R$ 6,00")
    )

    // Estado para o filtro (Todos, Aluno 1, Aluno 2)
    var selectedFilter by remember { mutableStateOf("Todos") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Extrato de Compras",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // --- Filtros (Botões superiores) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatementFilterChip(text = "Todos", selected = selectedFilter == "Todos") { selectedFilter = "Todos" }
                StatementFilterChip(text = "Aluno 1", selected = selectedFilter == "Aluno 1") { selectedFilter = "Aluno 1" }
                StatementFilterChip(text = "Aluno 2", selected = selectedFilter == "Aluno 2") { selectedFilter = "Aluno 2" }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Título da Seção ---
            Text(
                text = "Transações",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // --- Lista de Transações ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                val filteredList = if (selectedFilter == "Todos") transactions else transactions.filter { it.studentName == selectedFilter }

                items(filteredList) { item ->
                    TransactionRow(item)
                }
            }
        }
    }
}

// --- Componente: Botão de Filtro ---
@Composable
fun StatementFilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (selected) GreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

// --- Componente: Linha da Transação ---
@Composable
fun TransactionRow(item: PurchaseTransaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Coluna da Esquerda (Nome do item, Aluno, Data)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = item.studentName,
                fontSize = 14.sp,
                color = GreenPrimary,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(
                text = item.date,
                fontSize = 12.sp,
                color = GreenPrimary
            )
        }

        // Coluna da Direita (Preço)
        Text(
            text = item.price,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PurchaseStatementPreview() {
    PurchaseStatementScreen(nav = rememberNavController())
}
