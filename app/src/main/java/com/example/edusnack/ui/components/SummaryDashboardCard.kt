package com.example.edusnack.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
            .background(MaterialTheme.colorScheme.surfaceVariant) // COR DO TEMA
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
                color = MaterialTheme.colorScheme.primary, // COR DO TEMA (Verde)
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // COR DO TEMA (Valor)
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = description,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), // COR DO TEMA
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        // Lado Direito (Imagem Ilustrativa)
        Box(
            modifier = Modifier
                .size(100.dp, 70.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface) // COR DO TEMA
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
