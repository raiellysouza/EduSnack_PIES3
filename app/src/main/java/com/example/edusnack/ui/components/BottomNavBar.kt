package com.example.edusnack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Feedback

@Composable
fun BottomNavBar(nav: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface) // COR DO TEMA
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomItem("Cardápio", Icons.Filled.CalendarToday) {
            nav.navigate("dailyMenu")
        }

        BottomItem("Conta", Icons.Filled.Person) {
            nav.navigate("studentAccount")
        }

        BottomItem("Sobre", Icons.Filled.Feedback) {
            nav.navigate("canteenInfo")
        }
    }
}

@Composable
private fun BottomItem(
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
            tint = MaterialTheme.colorScheme.onSurface // COR DO TEMA
        )
        Spacer(Modifier.height(4.dp))
        Text(
            label, 
            fontSize = 12.sp, 
            color = MaterialTheme.colorScheme.onSurface // COR DO TEMA
        )
    }
}
