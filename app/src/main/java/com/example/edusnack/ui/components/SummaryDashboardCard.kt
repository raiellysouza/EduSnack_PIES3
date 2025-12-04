package com.example.edusnack.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun SummaryDashboardCard(
    title: String,
    value: String,
    description: String,
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lado Esquerdo (Textos)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = title,
                color = Color(0xFF4CAF50), // Verde Texto
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = value,
                color = Color.Black,
                fontSize = 28.sp, // Número Grande
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = description,
                color = Color(0xFF4CAF50),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        // Lado Direito (Imagem Ilustrativa)
        // Usamos um Box com cor de fundo pêssego/laranja claro conforme a imagem
        Box(
            modifier = Modifier
                .size(100.dp, 70.dp) // Formato retangular horizontal
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFCCBC)) // Cor pêssego do fundo da imagem
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop, // Ou Fit dependendo da sua imagem
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}