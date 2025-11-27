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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.edusnack.R

@Composable
fun BottomNavBar(nav: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomItem("Menu Diário", R.drawable.ic_calendar) {
            nav.navigate("home")
        }
        BottomItem("Antecipar", R.drawable.ic_clock) {}
        BottomItem("Conta", R.drawable.ic_user) {
            nav.navigate("conta")
        }
        BottomItem("Feedback", R.drawable.ic_feedback) {}
    }
}

@Composable
fun BottomItem(text: String, icon: Int, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(painterResource(icon), contentDescription = null)
        Text(text)
    }
}
