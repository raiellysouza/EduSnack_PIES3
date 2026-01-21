package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.edusnack.ui.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteenInfoScreen(nav: NavController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Informações",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {

            // --- SEÇÃO 1: Informações da Cantina ---
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Informações da Cantina",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }

            item {
                InfoItemRow(
                    icon = Icons.Filled.AccessTime,
                    title = "Horário de Funcionamento",
                    description = "Segunda a Sexta, 7:00 AM - 3:00 PM"
                )
                Spacer(modifier = Modifier.height(24.dp))

                InfoItemRow(
                    icon = Icons.Filled.Phone,
                    title = "Informações de Contato",
                    description = "Entre em contato conosco pelo (555) 123-4567"
                )
                Spacer(modifier = Modifier.height(24.dp))

                InfoItemRow(
                    icon = Icons.Filled.Campaign,
                    title = "Feedback",
                    description = "Para quaisquer dúvidas ou feedback, entre em contato com nossa equipe."
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- SEÇÃO 2: Anúncios ---
            item {
                Text(
                    text = "Anúncios",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }

            item {
                InfoItemRow(
                    icon = Icons.Filled.Event,
                    title = "Fechamento de Feriado",
                    description = "A cantina estará fechada em 4 de julho para o Dia da Independência."
                )
                Spacer(modifier = Modifier.height(24.dp))

                InfoItemRow(
                    icon = Icons.Filled.Eco,
                    title = "Novos Itens do Cardápio",
                    description = "Agora oferecemos uma variedade de opções vegetarianas e veganas. Confira"
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun InfoItemRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF4CAF50),
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CanteenInfoPreview() {
    CanteenInfoScreen(nav = rememberNavController())
}
