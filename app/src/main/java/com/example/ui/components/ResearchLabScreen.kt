package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomMachine
import com.example.data.model.Product
import com.example.data.model.ProductCatalog
import com.example.data.model.ProductCategory
import com.example.data.repository.GameState
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResearchLabScreen(
    state: GameState,
    onInventMachine: (List<Product>, String, (CustomMachine) -> Unit) -> Unit,
    onAssignToPlot: (Int, String?, String?) -> Unit
) {
    val selectedComponents = remember { mutableStateListOf<Product>() }
    var promptInput by remember { mutableStateOf("") }
    var recentlyCreatedMachine by remember { mutableStateOf<CustomMachine?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<ProductCategory?>(ProductCategory.MECHANICAL_PARTS) }

    val availableInCat = remember(selectedCategoryFilter, state.marketProducts) {
        state.marketProducts.filter { it.category == selectedCategoryFilter }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "core_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .padding(bottom = 80.dp),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Lab Header ---
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF131127)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF7C3AED), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Bancada de P&D & Síntese IA",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Combine insumos e maquinários para gerar protótipos únicos em tempo real",
                            color = Color(0xFFC4B5FD),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // --- The Assembly Workbench (Mesa de Montagem Digital) ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "MESA DE MONTAGEM DIGITAL (SLOTS DE PEÇAS)",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4 Assembly Slots
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (0 until 4).forEach { slotIndex ->
                            val component = selectedComponents.getOrNull(slotIndex)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(78.dp)
                                    .padding(horizontal = 4.dp)
                                    .background(
                                        if (component != null) Color(0xFF1E293B) else Color(0xFF020617),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (component != null) Color(0xFF7C3AED) else Color(0xFF334155),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (component != null) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remover",
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable { selectedComponents.remove(component) }
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.PrecisionManufacturing,
                                            contentDescription = null,
                                            tint = Color(0xFFA78BFA),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = component.name,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color(0xFF475569),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text("Slot ${slotIndex + 1}", color = Color(0xFF475569), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Optional Prompt Field
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        label = { Text("Diretriz Técnica / Nome da Máquina (Opcional)", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                        placeholder = { Text("Ex: Colheitadeira-moenda híbrida de alta rotação...", color = Color(0xFF64748B), fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_ai_prompt"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7C3AED),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // AI Generation Action Button
                    Button(
                        onClick = {
                            if (selectedComponents.isNotEmpty()) {
                                onInventMachine(selectedComponents.toList(), promptInput) { newMachine ->
                                    recentlyCreatedMachine = newMachine
                                    selectedComponents.clear()
                                    promptInput = ""
                                }
                            }
                        },
                        enabled = selectedComponents.isNotEmpty() && !state.isAiInventing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C3AED),
                            disabledContainerColor = Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_synthesize_ai")
                    ) {
                        if (state.isAiInventing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processando Síntese Física por IA...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sintetizar Maquinário Híbrido com IA",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // --- Just Generated Machine Showcase Card ---
        recentlyCreatedMachine?.let { machine ->
            item {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF1E1B4B)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, Color(0xFFA78BFA), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFF4C1D95),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "SUCESSO: PROTÓTIPO CRIADO POR IA",
                                    color = Color(0xFFDDD6FE),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            IconButton(onClick = { recentlyCreatedMachine = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color(0xFF94A3B8))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Custom Procedural Canvas Sprite
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(machine.primaryColorHex), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(56.dp)) {
                                    val center = Offset(size.width / 2, size.height / 2)
                                    val radius = size.minDimension / 2.6f

                                    // Outer ring
                                    drawCircle(
                                        color = Color(machine.primaryColorHex).copy(alpha = pulseAlpha),
                                        radius = radius,
                                        center = center,
                                        style = Stroke(width = 3.dp.toPx())
                                    )

                                    // Inner core
                                    drawCircle(
                                        color = Color(machine.secondaryColorHex),
                                        radius = radius * 0.45f,
                                        center = center
                                    )

                                    // Vector nodes
                                    for (i in 0 until 6) {
                                        val angle = Math.toRadians((i * 60.0) + rotationAngle.toDouble())
                                        val x = center.x + (radius * cos(angle)).toFloat()
                                        val y = center.y + (radius * sin(angle)).toFloat()
                                        drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(x, y))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = machine.name,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = machine.category,
                                    color = Color(0xFF38BDF8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Valor Estimado: $${String.format("%,.0f", machine.estimatedValue)}",
                                    color = Color(0xFF34D399),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dynamic Tags
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            TagBadge("Estado: ${machine.physicalState.label}", Color(0xFF0284C7))
                            TagBadge("Potência: ${machine.powerConsumptionKw} kW", Color(0xFFD97706))
                            TagBadge("Produção: x${machine.productionMultiplier}", Color(0xFF059669))
                            TagBadge("Peso: ${machine.weightKg} kg", Color(0xFF475569))
                            TagBadge("Durabilidade: ${machine.durabilityHrs}h", Color(0xFF7C3AED))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = machine.technicalSummary,
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                // Install on the first available plot
                                val openPlot = state.factoryPlots.firstOrNull { it.businessId == null && it.customMachineId == null }
                                if (openPlot != null) {
                                    onAssignToPlot(openPlot.plotIndex, null, machine.id)
                                    recentlyCreatedMachine = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Instalar Imediatamente em Lote Fabril Vazio", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // --- Available Components Selector ---
        item {
            Text(
                text = "SELECIONE OS COMPONENTES PARA A BANCADA",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 4.dp)
            )

            // Category Filter Bar
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                val categories = listOf(
                    ProductCategory.MECHANICAL_PARTS,
                    ProductCategory.ENERGY_BASIC,
                    ProductCategory.RAW_NATURAL,
                    ProductCategory.ADVANCED_CHEMICAL,
                    ProductCategory.HIGH_TECH_SPACE
                )
                items(categories) { cat ->
                    val isSelected = selectedCategoryFilter == cat
                    Surface(
                        color = if (isSelected) Color(0xFF7C3AED) else Color(0xFF1E293B),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { selectedCategoryFilter = cat }
                    ) {
                        Text(
                            text = cat.displayName,
                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Component Grid Cards
        items(availableInCat) { prod ->
            val isInBench = selectedComponents.any { it.id == prod.id }
            val inventoryCount = state.inventory[prod.id] ?: 0

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isInBench) Color(0xFF1E1B4B) else Color(0xFF0F172A)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isInBench) {
                            selectedComponents.removeAll { it.id == prod.id }
                        } else if (selectedComponents.size < 4) {
                            selectedComponents.add(prod)
                        }
                    }
                    .border(
                        1.dp,
                        if (isInBench) Color(0xFFA78BFA) else Color(0xFF1E293B),
                        RoundedCornerShape(10.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PrecisionManufacturing,
                                contentDescription = null,
                                tint = if (isInBench) Color(0xFFA78BFA) else Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = prod.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${prod.physicalState.label} • Densidade: ${prod.densityKgM3.toInt()}kg/m³ • Reatividade: ${prod.reactivity}",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${prod.currentMarketPrice.toInt()}/un",
                            color = Color(0xFF34D399),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Estoque: $inventoryCount un",
                            color = if (inventoryCount > 0) Color(0xFF38BDF8) else Color(0xFF64748B),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TagBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
