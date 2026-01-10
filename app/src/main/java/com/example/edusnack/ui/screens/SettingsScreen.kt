package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.edusnack.ui.theme.GrayBackground
import com.example.edusnack.ui.theme.GreenPrimary
import com.example.edusnack.ui.theme.DarkText
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(nav: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Configurações", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color.White)
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)) {

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Configurações", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))

                // Row 1: Canteen data
                SettingsRow(
                    icon = Icons.Filled.Restaurant,
                    label = "Dados do restaurante/cantina",
                    onClick = { nav.navigate("canteenInfo") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Row 2: Operating hours
                SettingsRow(
                    icon = Icons.Filled.AccessTime,
                    label = "Horário de funcionamento",
                    onClick = { nav.navigate("canteenInfo") }
                )

                // Push the logout button to the bottom
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        nav.navigate("login") { popUpTo("welcome") { inclusive = true } }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text(text = "Sair", color = Color.Black, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = GrayBackground,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}
