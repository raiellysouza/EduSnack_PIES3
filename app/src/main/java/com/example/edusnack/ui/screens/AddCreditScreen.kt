package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edusnack.model.Aluno
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.viewmodel.CreditViewModel

val InputBackground = Color(0xFFE8F5E9)
val PrimaryGreen = Color(0xFF00E676)
val TextGreen = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCreditScreen(nav: NavController, vm: CreditViewModel = viewModel()) {
    var selectedAmount by remember { mutableStateOf("0,00") }
    val children by vm.children.collectAsState()
    val loading by vm.loading.collectAsState()
    val success by vm.success.collectAsState()

    var selectedChild by remember { mutableStateOf<Aluno?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(success) {
        if (success) {
            nav.navigate("rechargeSuccess")
            vm.resetSuccess()
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
                    text = "Adicionar Crédito",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Selecione o Aluno",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Dropdown real para selecionar aluno
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedChild?.nomeCompleto ?: "Selecione",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBackground,
                        unfocusedContainerColor = InputBackground,
                        disabledContainerColor = InputBackground,
                    ),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    children.forEach { child ->
                        DropdownMenuItem(
                            text = { Text(child.nomeCompleto) },
                            onClick = {
                                selectedChild = child
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Valor a Adicionar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(InputBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "R$ ", color = TextGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = selectedAmount, color = TextGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAmountChip(label = "R$10", onClick = { selectedAmount = "10,00" })
                QuickAmountChip(label = "R$20", onClick = { selectedAmount = "20,00" })
                QuickAmountChip(label = "R$50", onClick = { selectedAmount = "50,00" })
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Método de Pagamento",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, InputBackground, RoundedCornerShape(8.dp))
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Pix", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Icon(imageVector = Icons.Default.RadioButtonChecked, contentDescription = "Selecionado", tint = PrimaryGreen, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = PrimaryGreen)
            } else {
                Button(
                    onClick = {
                        val amount = selectedAmount.replace(",", ".").toDoubleOrNull() ?: 0.0
                        selectedChild?.let { vm.addCredit(it.id, amount) }
                    },
                    enabled = selectedChild != null && selectedAmount != "0,00",
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text(text = "Confirmar Adicionar Crédito", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickAmountChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(70.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(InputBackground)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}
