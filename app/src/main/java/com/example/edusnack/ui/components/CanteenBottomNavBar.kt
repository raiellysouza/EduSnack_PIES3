package com.example.edusnack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edusnack.ui.theme.DarkText

@Composable
fun CanteenBottomNavBar(nav: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CanteenBottomItem("Início", Icons.AutoMirrored.Filled.List) { nav.navigate("homeCantina") }
        CanteenBottomItem("Cardápio", Icons.Default.CalendarToday) { nav.navigate("manage_menu") }
        CanteenBottomItem("Pedidos", Icons.Default.ListAlt) { nav.navigate("view_orders") }
        CanteenBottomItem("Conta", Icons.Default.Person) { nav.navigate("canteen_settings") }
        CanteenBottomItem("Relatórios", Icons.Default.Analytics) { /* nav.navigate("reports") */ }
    }
}

@Composable
private fun CanteenBottomItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = DarkText, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = DarkText)
    }
}