package com.example.edusnack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Feedback

@Composable
fun BottomNavBar(nav: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomItem("Menu Diário", Icons.Filled.CalendarToday) {
            nav.navigate("dailyMenu")
        }

        BottomItem("Antecipar", Icons.Filled.AccessTime) {
            nav.navigate("advanceOrder")
        }

        BottomItem("Conta", Icons.Filled.Person) {
            nav.navigate("studentAccount")
        }

        BottomItem("Feedback", Icons.Filled.Feedback) {
            // futura funcionalidade
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
        Icon(icon, contentDescription = label)
        Spacer(Modifier.height(4.dp))
        Text(label)
    }
}
