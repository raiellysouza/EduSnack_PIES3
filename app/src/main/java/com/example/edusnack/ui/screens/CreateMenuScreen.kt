package com.example.edusnack.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
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
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMenuScreen(nav: NavController, vm: CardapioViewModel = viewModel()) {
    // Estados dos campos
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }

    // Categoria (dropdown)
    val categorias = remember { listOf("Salgados", "Sucos", "Bolos", "Lanches") }
    var categoria by remember { mutableStateOf("Lanches") }

    // Estado da Imagem
    var imagemUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagemUri = uri
    }

    // Disponibilidade (dias da semana) - default: Seg–Sex
    var selectedWeekdays by remember {
        mutableStateOf(
            setOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
            )
        )
    }

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

            // --- CATEGORIA (LISTA) ---
            item {
                CategoryDropdownField(
                    value = categoria,
                    options = categorias,
                    onSelect = { categoria = it },
                    label = "Categoria"
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // --- DISPONIBILIDADE (DIAS DA SEMANA) ---
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
                WeekdayMultiSelectField(
                    selectedDays = selectedWeekdays,
                    onChange = { selectedWeekdays = it },
                    label = "Dias da semana"
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
                            if (nome.isBlank() || precoDouble == null) return@Button

                            // ✅ Dias da semana como lista de strings: ["MONDAY","TUESDAY",...]
                            val diasDisponiveis = selectedWeekdays
                                .sortedBy { it.value % 7 } // Dom(0) ... Sáb(6) se você quiser Dom primeiro
                                .map { it.name }

                            val item = Cardapio(
                                nome = nome.trim(),
                                descricao = descricao.trim(),
                                preco = precoDouble,
                                categoria = categoria.trim(),
                                possuiLactose = contemLactose,
                                possuiGluten = contemGluten,
                                vegano = vegano,
                                vegetariano = vegano,
                                autorId = "admin",
                                diasDisponiveis = diasDisponiveis
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdownField(
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(label, color = Color(0xFF4CAF50)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE9F2E8),
                unfocusedContainerColor = Color(0xFFE9F2E8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF4CAF50)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun WeekdayMultiSelectField(
    selectedDays: Set<DayOfWeek>,
    onChange: (Set<DayOfWeek>) -> Unit,
    label: String
) {
    var open by remember { mutableStateOf(false) }

    val ordered = listOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )

    val summary = remember(selectedDays) {
        if (selectedDays.isEmpty()) label
        else ordered
            .filter { selectedDays.contains(it) }
            .joinToString(", ") { it.toPtBrShort() }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { open = true },
        color = Color(0xFFE9F2E8)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = summary,
                color = if (selectedDays.isEmpty()) Color(0xFF4CAF50) else DarkText,
                modifier = Modifier.weight(1f)
            )
            Text("▼", color = Color(0xFF4CAF50))
        }
    }

    if (open) {
        WeekdayMultiSelectDialog(
            initialSelected = selectedDays,
            onDismiss = { open = false },
            onConfirm = {
                onChange(it)
                open = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeekdayMultiSelectDialog(
    initialSelected: Set<DayOfWeek>,
    onDismiss: () -> Unit,
    onConfirm: (Set<DayOfWeek>) -> Unit
) {
    var selected by remember { mutableStateOf(initialSelected) }

    val ordered = listOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecionar dias", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Ações rápidas
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = { selected = ordered.toSet() },
                        label = { Text("Todos") }
                    )
                    AssistChip(
                        onClick = { selected = emptySet() },
                        label = { Text("Limpar") }
                    )
                    AssistChip(
                        onClick = {
                            selected = setOf(
                                DayOfWeek.MONDAY,
                                DayOfWeek.TUESDAY,
                                DayOfWeek.WEDNESDAY,
                                DayOfWeek.THURSDAY,
                                DayOfWeek.FRIDAY
                            )
                        },
                        label = { Text("Seg–Sex") }
                    )
                }

                // Chips dos dias
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ordered.forEach { day ->
                        val isSelected = selected.contains(day)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selected = if (isSelected) selected - day else selected + day
                            },
                            label = {
                                Text(
                                    text = day.toPtBrShort(),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFB9F6CA),
                                containerColor = Color.Transparent
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color(0xFF4CAF50),
                                selectedBorderColor = Color(0xFF2E7D32),
                                borderWidth = 1.dp
                            )
                        )
                    }
                }

                if (selected.isNotEmpty()) {
                    val preview = ordered
                        .filter { selected.contains(it) }
                        .joinToString(", ") { it.toPtBrShort() }

                    Text(
                        "Selecionados: $preview",
                        fontSize = 12.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selected) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar", color = DarkText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

private fun DayOfWeek.toPtBrShort(): String {
    return when (this) {
        DayOfWeek.SUNDAY -> "Dom"
        DayOfWeek.MONDAY -> "Seg"
        DayOfWeek.TUESDAY -> "Ter"
        DayOfWeek.WEDNESDAY -> "Qua"
        DayOfWeek.THURSDAY -> "Qui"
        DayOfWeek.FRIDAY -> "Sex"
        DayOfWeek.SATURDAY -> "Sáb"
    }
}
