package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.edusnack.model.Cardapio
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.viewmodel.CardapioViewModel
import com.example.edusnack.viewmodel.CarrinhoViewModel

// Cores privadas pra não conflitar com outros arquivos
private val DetailsBrightGreenButton = Color(0xFF00E676)
private val DetailsLabelGreen = Color(0xFF4CAF50)

@Composable
fun ItemDetailsScreen(
    nav: NavController,
    itemId: String,
    cardapioVm: CardapioViewModel = viewModel(),
    carrinhoVm: CarrinhoViewModel
) {
    val item by cardapioVm.itemSelecionado.collectAsState()
    
    // Estado para os dias da semana selecionados
    val diasSemana = listOf("Seg", "Ter", "Qua", "Qui", "Sex")
    val selecionados = remember { mutableStateListOf<String>() }

    // Busca real quando entra na tela
    LaunchedEffect(itemId) {
        if (itemId.isNotBlank()) {
            cardapioVm.carregarItem(itemId)
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = { nav.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.Black
                    )
                }
                Text(
                    text = "Detalhes do Item",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->

        if (item == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text("Carregando item...", color = DetailsLabelGreen)
            }
            return@Scaffold
        }

        val i = item!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = i.imagemUrl ?: "https://placehold.co/600x400/png?text=Item",
                contentDescription = i.nome,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = i.nome,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = i.descricao,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // --- Seleção de Dias da Semana ---
            Text(
                text = "Quais dias você quer reservar?",
                color = DetailsLabelGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                diasSemana.forEach { dia ->
                    val isSelected = selecionados.contains(dia)
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) DetailsBrightGreenButton else Color(0xFFF5F5F5))
                            .clickable { 
                                if (isSelected) selecionados.remove(dia) else selecionados.add(dia)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dia,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Preço Unitário",
                        color = DetailsLabelGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "R$ %.2f".format(i.preco ?: 0.0).replace('.', ','),
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total Semanal",
                        color = DetailsLabelGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val total = (i.preco ?: 0.0) * selecionados.size
                    Text(
                        text = "R$ %.2f".format(total).replace('.', ','),
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (selecionados.isNotEmpty()) {
                        carrinhoVm.adicionar(i) 
                        nav.navigate("carrinho")
                    }
                },
                enabled = selecionados.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DetailsBrightGreenButton,
                    disabledContainerColor = Color.LightGray
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (selecionados.isEmpty()) "Selecione os dias" else "Confirmar Pedido",
                    color = if (selecionados.isEmpty()) Color.Gray else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
