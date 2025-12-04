package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.edusnack.ui.components.BottomNavBar

// --- Cores Base ---
val LightBackground = Color(0xFFF5F5F5)

// --- Modelos de Dados (Contexto do Responsável) ---

// Representa o filho/dependente na visão do pai
data class ChildDependentItem(
    val name: String,
    val details: String, // Ex: "Turma: 8A | Saldo: R$ 25,00"
    val placeholderIcon: ImageVector = Icons.Default.Person
)

// Transação resumida para o histórico
data class TransactionSummary(
    val title: String,
    val studentName: String,
    val date: String,
    val price: String
)

@Composable
fun ParentAccountScreen(nav: NavController) {
    // Dados Mockados: Lista de filhos associados a este pai
    val childrenList = listOf(
        ChildDependentItem("Lucas Silva", "Turma: 8A | Saldo: R$ 25,00"),
        ChildDependentItem("Sofia Silva", "Turma: 6B | Saldo: R$ 18,50")
    )

    // Dados Mockados: Última transação realizada por um dos dependentes
    val recentTransaction = TransactionSummary(
        "Sanduíche de Frango",
        "Aluno 1",
        "12/07/2024 14:30",
        "R$ 8,50"
    )

    Scaffold(
        topBar = {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = { nav.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.Black
                    )
                }
                Text(
                    text = "Conta do Responsável", // Título ajustado para clareza (opcional, pode manter só "Conta")
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LightBackground)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Seção do Perfil do Pai/Mãe ---
            item {
                Spacer(modifier = Modifier.height(24.dp))
                // Foto do Responsável
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFCCBC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto do Responsável",
                        modifier = Modifier.size(80.dp),
                        tint = Color(0xFF8D6E63)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Letícia Silva", // Nome do Pai/Mãe
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- Seção: Meus Dependentes ---
            item {
                ParentSectionTitle(text = "Meus Dependentes")
            }

            items(childrenList) { child ->
                ChildDependentRow(child)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- Seção: Histórico de Transações ---
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ParentSectionTitle(text = "Histórico de Transações")
            }

            item {
                TransactionSummaryRow(recentTransaction)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// --- Componentes Auxiliares ---

@Composable
fun ParentSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        textAlign = TextAlign.Start
    )
}

@Composable
fun ChildDependentRow(child: ChildDependentItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar do Filho
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFE0B2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = child.placeholderIcon,
                contentDescription = "Foto de ${child.name}",
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF795548)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Dados do Filho
        Column {
            Text(
                text = child.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = child.details,
                fontSize = 14.sp,
                color = AppGreen
            )
        }
    }
}

@Composable
fun TransactionSummaryRow(item: TransactionSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = item.studentName,
                fontSize = 14.sp,
                color = AppGreen,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(
                text = item.date,
                fontSize = 12.sp,
                color = AppGreen
            )
        }
        Text(
            text = item.price,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ParentAccountScreenPreview() {
    ParentAccountScreen(nav = rememberNavController())
}