package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.edusnack.ui.components.BottomNavBar

// Modelo de dados para os dependentes
data class Dependent(
    val name: String,
    val classInfo: String, // Ex: "8A"
    val balance: Double,
    val imageUrl: String
)

// Cores extraídas da imagem

@Composable
fun MyDependentsScreen(nav: NavController) {
    // Dados simulados
    val dependents = listOf(
        Dependent("Lucas Silva", "8A", 25.00, "https://placehold.co/200x200/FFCCBC/ffffff?text=LS"),
        Dependent("Sofia Silva", "6B", 18.50, "https://placehold.co/200x200/FFCCBC/ffffff?text=SS")
    )

    Scaffold(
        topBar = {
            // Título Centralizado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Meus Dependentes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        },
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(horizontal = 24.dp)
        ) {
            // --- LISTA DE DEPENDENTES ---
            items(dependents) { dependent ->
                DependentItem(dependent)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // --- SEÇÃO "OPÇÕES" ---
            item {
                Text(
                    text = "Opções",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // --- BOTÃO "ADICIONAR FUNDOS" ---
            item {
                AddFundsOption(onClick = { /* Navegar para tela de adicionar fundos */ })
            }
        }
    }
}

// Componente para cada item da lista de dependentes
@Composable
fun DependentItem(dependent: Dependent) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Foto de Perfil
        AsyncImage(
            model = dependent.imageUrl,
            contentDescription = "Foto de ${dependent.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFCCBC)) // Placeholder
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Informações (Nome, Turma e Saldo)
        Column {
            Text(
                text = dependent.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Turma: ${dependent.classInfo} | Saldo: R$ %.2f".format(dependent.balance).replace('.', ','),
                fontSize = 14.sp,
                color = AppGreen, // Cor verde
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AddFundsOption(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Ícone de Mais no fundo verde claro
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(LightGreenBackground, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Texto
            Text(
                text = "Adicionar Fundos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        // Seta para a direita
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyDependentsScreenPreview() {
    MyDependentsScreen(nav = rememberNavController())
}