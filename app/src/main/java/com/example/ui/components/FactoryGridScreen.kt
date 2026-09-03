package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessCatalog
import com.example.data.model.BusinessType
import com.example.data.model.CustomMachine
import com.example.data.model.FactoryPlot
import com.example.data.model.ProductCatalog
import com.example.data.repository.GameState
import kotlin.math.roundToInt

@Composable
fun FactoryGridScreen(
    state: GameState,
    onUpgradePlot: (Int) -> Unit,
    onAssignPlot: (Int, String?, String?) -> Unit,
    onQuickSell: (String, Int) -> Unit,
    onNavigateToEnterprises: () -> Unit,
    onNavigateToResearch: () -> Unit
) {
    var selectedPlotForModal by remember { mutableStateOf<FactoryPlot?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "gear_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_deg"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19)) // Industrial dark slate
            .padding(bottom = 80.dp),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Header / Industrial Overview Card ---
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFF131B2E)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Factory,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Planta Fabril Principal",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${state.factoryPlots.count { it.businessId != null || it.customMachineId != null }}/12 Lotes Industriais Ativos",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = onNavigateToEnterprises,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("btn_add_business")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Comprar Negócio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Metrics Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MetricItem(
                            label = "Patrimônio Líquido",
                            value = "$${String.format("%,.0f", state.netWorth)}",
                            color = Color(0xFF34D399)
                        )
                        MetricItem(
                            label = "Carga Energética",
                            value = "${state.totalEnergyConsumedKw.roundToInt()} kW",
                            color = Color(0xFFFBBF24)
                        )
                        MetricItem(
                            label = "Criações IA",
                            value = "${state.customMachines.size} Máquinas",
                            color = Color(0xFFA78BFA)
                        )
                    }
                }
            }
        }

        // --- 2D / Isometric Top-Down Grid of Factory Plots ---
        item {
            Text(
                text = "GRADE OPERACIONAL (2D ISOMÉTRICA)",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            // Grid Layout (2 columns for phone readability)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val chunkedPlots = state.factoryPlots.chunked(2)
                chunkedPlots.forEach { rowPlots ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowPlots.forEach { plot ->
                            Box(modifier = Modifier.weight(1f)) {
                                FactoryPlotCard(
                                    plot = plot,
                                    state = state,
                                    rotation = rotation,
                                    onClick = { selectedPlotForModal = plot }
                                )
                            }
                        }
                        if (rowPlots.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // --- Fast Inventory & Warehouse Strip ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Armazém & Inventário Atual (${state.inventory.values.sum()} un)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.inventory.isEmpty()) {
                        Text(
                            text = "Nenhum insumo estocado. Instale empresas para iniciar a colheita!",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(state.inventory.toList()) { (prodId, qty) ->
                                val product = state.marketProducts.find { it.id == prodId }
                                    ?: ProductCatalog.findById(prodId)
                                if (product != null) {
                                    InventoryChip(
                                        product = product,
                                        quantity = qty,
                                        onQuickSell = { onQuickSell(prodId, qty) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Plot Details / Actions Modal Dialog ---
    selectedPlotForModal?.let { plot ->
        val biz = plot.businessId?.let { BusinessCatalog.findById(it) }
        val machine = plot.customMachineId?.let { id -> state.customMachines.find { it.id == id } }
        val upgradeCost = 2500.0 * (plot.level * 1.5)

        AlertDialog(
            onDismissRequest = { selectedPlotForModal = null },
            containerColor = Color(0xFF1E293B),
            title = {
                Text(
                    text = "Lote Industrial #${plot.plotIndex + 1}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (biz != null) {
                        Text("Empresa Instalada: ${biz.name}", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                        Text("Setor: ${biz.sector.label}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text("Nível Atual: ${plot.level}", color = Color(0xFF34D399), fontWeight = FontWeight.SemiBold)
                        Text("Consumo Energético: ${biz.recipe.energyCostKw * plot.level} kW", color = Color(0xFFFBBF24), fontSize = 12.sp)
                        val outProd = ProductCatalog.findById(biz.recipe.outputItemId)
                        Text("Saída: +${biz.recipe.outputQuantity * plot.level} ${outProd?.name ?: "Item"} a cada ${biz.recipe.cycleTimeSeconds}s", color = Color.White, fontSize = 12.sp)
                    } else if (machine != null) {
                        Text("Maquinário IA: ${machine.name}", color = Color(0xFFA78BFA), fontWeight = FontWeight.Bold)
                        Text("Categoria: ${machine.category}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text("Multiplicador IA: x${machine.productionMultiplier}", color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                        Text("Consumo: ${machine.powerConsumptionKw} kW | Durabilidade: ${machine.durabilityHrs}h", color = Color(0xFFFBBF24), fontSize = 12.sp)
                        Text(machine.technicalSummary, color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    } else {
                        Text("Lote Vazio. Escolha um maquinário personalizado ou empresa para instalar neste lote.", color = Color(0xFF94A3B8))
                    }
                }
            },
            confirmButton = {
                if (biz != null || machine != null) {
                    Button(
                        onClick = {
                            onUpgradePlot(plot.plotIndex)
                            selectedPlotForModal = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        enabled = state.playerCash >= upgradeCost
                    ) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Aprimorar ($${upgradeCost.roundToInt()})")
                    }
                } else {
                    Button(
                        onClick = {
                            selectedPlotForModal = null
                            onNavigateToEnterprises()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("Selecionar Empresa")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPlotForModal = null }) {
                    Text("Fechar", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}

@Composable
private fun FactoryPlotCard(
    plot: FactoryPlot,
    state: GameState,
    rotation: Float,
    onClick: () -> Unit
) {
    val biz = plot.businessId?.let { BusinessCatalog.findById(it) }
    val machine = plot.customMachineId?.let { id -> state.customMachines.find { it.id == id } }
    val isEmpty = biz == null && machine == null

    val borderColor = when {
        machine != null -> Color(0xFFA78BFA)
        biz != null -> Color(0xFF0284C7)
        else -> Color(0xFF334155)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isEmpty) Color(0xFF0F172A) else Color(0xFF1E293B)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Plot header with index and level
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${plot.plotIndex + 1}",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                if (!isEmpty) {
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Lvl ${plot.level}",
                            color = Color(0xFF38BDF8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Visual Center Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isEmpty) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Lote Livre",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(22.dp)
                        )
                        Text("Lote Livre", color = Color(0xFF64748B), fontSize = 10.sp)
                    }
                } else if (machine != null) {
                    Icon(
                        imageVector = Icons.Default.PrecisionManufacturing,
                        contentDescription = null,
                        tint = Color(machine.primaryColorHex),
                        modifier = Modifier
                            .size(32.dp)
                            .rotate(rotation)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Factory,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Title & Output
            Text(
                text = machine?.name ?: biz?.name ?: "Disponível para montagem",
                color = if (isEmpty) Color(0xFF64748B) else Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!isEmpty) {
                // Animated Progress Bar
                LinearProgressIndicator(
                    progress = { plot.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (machine != null) Color(0xFFA78BFA) else Color(0xFF38BDF8),
                    trackColor = Color(0xFF0F172A),
                )
            }
        }
    }
}

@Composable
private fun InventoryChip(
    product: com.example.data.model.Product,
    quantity: Int,
    onQuickSell: () -> Unit
) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Column {
                Text(
                    text = product.name,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = "$quantity un • $${(product.currentMarketPrice * quantity).roundToInt()}",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Surface(
                color = Color(0xFF064E3B),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.clickable { onQuickSell() }
            ) {
                Text(
                    text = "Vender",
                    color = Color(0xFF34D399),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 10.sp)
        Text(
            text = value,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
        )
    }
}
