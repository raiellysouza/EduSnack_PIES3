package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMenuScreen(nav: NavController) {
    // Estados dos campos
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }

    // Estados de Disponibilidade
    var datas by remember { mutableStateOf("") }
    var dias by remember { mutableStateOf("") }

    // Estados dos Checkboxes
    var contemLactose by remember { mutableStateOf(false) }
    var contemGluten by remember { mutableStateOf(false) }
    var vegano by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Adicionar item",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = DarkText
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White // Garante fundo branco na tela toda
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // --- CAMPOS DE TEXTO PERSONALIZADOS ---

            item {
                CustomTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = "Nome do prato/lanche"
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                CustomTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = "Descrição",
                    modifier = Modifier.height(120.dp),
                    singleLine = false // Permite quebrar linha na descrição
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                CustomTextField(
                    value = preco,
                    onValueChange = { preco = it },
                    label = "Preço",
                    keyboardType = KeyboardType.Decimal // Teclado numérico
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                CustomTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = "Categoria"
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // --- DISPONIBILIDADE ---
            item {
                Text(
                    "Disponibilidade",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DarkText
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                CustomTextField(
                    value = datas,
                    onValueChange = { datas = it },
                    label = "Datas"
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                CustomTextField(
                    value = dias,
                    onValueChange = { dias = it },
                    label = "Dias"
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // --- RESTRIÇÕES ALIMENTARES ---
            item {
                Text(
                    "Restrições alimentares",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DarkText
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = contemLactose,
                        onCheckedChange = { contemLactose = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
                    )
                    Text("Contém lactose", color = DarkText)
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = contemGluten,
                        onCheckedChange = { contemGluten = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
                    )
                    Text("Contém glúten", color = DarkText)
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = vegano,
                        onCheckedChange = { vegano = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
                    )
                    Text("Vegano/Vegetariano", color = DarkText)
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }

            // --- BOTÕES ---
            item {
                Row(Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { nav.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Voltar", color = DarkText, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { /* TODO: Salvar item */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Salvar", color = DarkText, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

// --- COMPONENTE CUSTOMIZADO PARA OS CAMPOS ---
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = Color(0xFF4CAF50)) }, // Texto verde placeholder
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // Borda arredondada
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE9F2E8), // Fundo verde claro
            unfocusedContainerColor = Color(0xFFE9F2E8),
            focusedIndicatorColor = Color.Transparent, // Remove linha de baixo
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color(0xFF4CAF50)
        )
    )
}