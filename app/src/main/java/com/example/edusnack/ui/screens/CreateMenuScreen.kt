package com.example.edusnack.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.edusnack.model.Cardapio
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GreenPrimary
import com.example.edusnack.viewmodel.CardapioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMenuScreen(nav: NavController, vm: CardapioViewModel = viewModel()) {
    // Estados dos campos
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }

    // Estado da Imagem
    var imagemUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagemUri = uri
    }

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
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // --- SELEÇÃO DE IMAGEM ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF5F5F5))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imagemUri != null) {
                        AsyncImage(
                            model = imagemUri,
                            contentDescription = "Imagem selecionada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Adicionar foto do item", color = Color.Gray)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // --- CAMPOS DE TEXTO ---
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
                    singleLine = false
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                CustomTextField(
                    value = preco,
                    onValueChange = { preco = it },
                    label = "Preço",
                    keyboardType = KeyboardType.Decimal
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
                        onClick = {
                            val precoDouble = preco.trim().replace(",", ".").toDoubleOrNull()
                            if (nome.isBlank() || precoDouble == null) {
                                return@Button
                            }

                            val item = Cardapio(
                                nome = nome.trim(),
                                descricao = descricao.trim(),
                                preco = precoDouble,
                                categoria = if (categoria.isBlank()) "Lanches" else categoria.trim(),
                                possuiLactose = contemLactose,
                                possuiGluten = contemGluten,
                                vegano = vegano,
                                vegetariano = vegano,
                                autorId = "admin"
                            )

                            vm.salvarItem(
                                item = item,
                                imagemUri = imagemUri,
                                onSuccess = { nav.popBackStack() }
                            )
                        },
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
        placeholder = { Text(label, color = Color(0xFF4CAF50)) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE9F2E8),
            unfocusedContainerColor = Color(0xFFE9F2E8),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color(0xFF4CAF50)
        )
    )
}
