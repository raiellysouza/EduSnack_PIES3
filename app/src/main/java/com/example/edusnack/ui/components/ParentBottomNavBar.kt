package com.example.edusnack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ParentBottomNavBar(nav: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // CORREÇÃO: "Início" agora leva para a tela de dependentes, que é a sua tela principal
        ParentBottomItem("Início", Icons.Default.People) {
            nav.navigate("myDependents") {
                popUpTo("myDependents") { inclusive = true }
            }
        }

        ParentBottomItem("Créditos", Icons.Default.Payments) {
            nav.navigate("addCredit")
        }

        // Alterado para "Conta" levar para o resumo financeiro (ParentAccount)
        ParentBottomItem("Conta", Icons.Default.Home) {
            nav.navigate("parentAccount")
        }

        ParentBottomItem("Extrato", Icons.Default.History) {
            nav.navigate("purchaseStatement")
        }
    }
}

@Composable
private fun ParentBottomItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon, 
            contentDescription = label, 
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            label, 
            fontSize = 10.sp, 
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
