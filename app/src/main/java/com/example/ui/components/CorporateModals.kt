package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameEra
import com.example.data.repository.GameState
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun EraProgressionDialog(
    state: GameState,
    onDismiss: () -> Unit
) {
    val eras = GameEra.values()
    val nextEra = eras.firstOrNull { it.stage == state.currentEra.stage + 1 }
    val progressToNext = if (nextEra != null) {
        val currentBase = state.currentEra.requiredNetWorth
        val target = nextEra.requiredNetWorth
        val currentProgress = state.netWorth - currentBase
        val needed = target - currentBase
        (currentProgress / needed).coerceIn(0.0, 1.0).toFloat()
    } else 1.0f

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Progressão de Era Corporativa", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Next Era Progress
                if (nextEra != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Rumo à Era ${nextEra.stage}: ${nextEra.title}", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("${(progressToNext * 100).toInt()}%", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progressToNext },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFF38BDF8),
                                trackColor = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Patrimônio: $${String.format("%,.0f", state.netWorth)} / $${String.format("%,.0f", nextEra.requiredNetWorth)}",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // All 5 Eras List
                eras.forEach { era ->
                    val isReached = state.currentEra.stage >= era.stage
                    val isCurrent = state.currentEra == era

                    Surface(
                        color = if (isCurrent) Color(0xFF1E3A8A) else if (isReached) Color(0xFF1E293B) else Color(0xFF0B0F19),
                        shape = RoundedCornerShape(8.dp),
                        border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            if (isReached) Color(0xFF059669) else Color(0xFF334155),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isReached) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    } else {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(12.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${era.stage}. ${era.title}",
                                        color = if (isReached) Color.White else Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = era.description,
                                        color = Color(0xFF64748B),
                                        fontSize = 9.sp,
                                        maxLines = 1
                                    )
                                }
                            }

                            Text(
                                text = if (era.requiredNetWorth == 0.0) "Início" else "$${String.format("%,.0f", era.requiredNetWorth)}",
                                color = if (isReached) Color(0xFF34D399) else Color(0xFF64748B),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))) {
                Text("Entendido")
            }
        }
    )
}

@Composable
fun FinancesDialog(
    state: GameState,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Demonstrativo Financeiro & Macro", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FinanceRow("Caixa Disponível (Livre)", "$${String.format("%,.2f", state.playerCash)}", Color(0xFF34D399))
                FinanceRow("Patrimônio Líquido Total", "$${String.format("%,.0f", state.netWorth)}", Color(0xFF38BDF8))
                FinanceRow("Empresas Instaladas", "${state.ownedBusinesses.values.sum()} plantas", Color.White)
                FinanceRow("Rotas de Frotas", "${state.fleetRoutes.size} corredores", Color.White)
                FinanceRow("Geração Elétrica Total", "${state.totalEnergyGeneratedKw.roundToInt()} kW", Color(0xFFFBBF24))
                FinanceRow("Consumo Elétrico Total", "${state.totalEnergyConsumedKw.roundToInt()} kW", Color(0xFFF87171))
                FinanceRow("Taxa Tributária Corporativa", "${state.taxRatePct}% sobre vendas", Color(0xFFA78BFA))
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))) {
                Text("Fechar")
            }
        }
    )
}

@Composable
private fun FinanceRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFF94A3B8), fontSize = 11.sp)
        Text(
            value,
            color = valueColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
