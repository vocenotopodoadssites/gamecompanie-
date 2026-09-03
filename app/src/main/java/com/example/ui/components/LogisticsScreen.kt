package com.example.ui.components

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsRailway
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FleetRoute
import com.example.data.model.VehicleType
import com.example.data.repository.GameState
import kotlin.math.roundToInt

@Composable
fun LogisticsScreen(
    state: GameState,
    onAddRoute: (VehicleType, String, String, String) -> Unit
) {
    var showNewRouteModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(bottom = 80.dp),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF131B2E)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF0D9488), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Central Logística & Frotas Intermodais",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Transporte rodoviário, ferroviário, marítimo, aéreo e suborbital",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { showNewRouteModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_new_route")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Nova Rota", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Routes
        item {
            Text(
                text = "ROTAS LOGÍSTICAS EM ANDAMENTO (${state.fleetRoutes.size})",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        items(state.fleetRoutes) { route ->
            RouteCardItem(route = route)
        }

        // Vehicle Classes Catalog
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "CLASSES DE TRANSPORTE DISPONÍVEIS",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        items(VehicleType.values()) { vType ->
            val isUnlocked = state.currentEra.stage >= vType.unlockEra.stage
            VehicleTypeItem(vType = vType, isUnlocked = isUnlocked)
        }
    }

    // Modal to create route
    if (showNewRouteModal) {
        var selectedVehicle by remember { mutableStateOf(VehicleType.TRUCK) }
        var routeName by remember { mutableStateOf("Corredor Logístico Expresso") }
        var origin by remember { mutableStateOf("Polo Central") }
        var dest by remember { mutableStateOf("Porto de Exportação") }

        AlertDialog(
            onDismissRequest = { showNewRouteModal = false },
            containerColor = Color(0xFF1E293B),
            title = { Text("Contratar Nova Rota de Transporte", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Selecione o modal de transporte:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    VehicleType.values().forEach { v ->
                        val isSel = selectedVehicle == v
                        val unlocked = state.currentEra.stage >= v.unlockEra.stage
                        Surface(
                            color = if (isSel) Color(0xFF0D9488) else Color(0xFF0F172A),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = unlocked) { selectedVehicle = v }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(v.title, color = if (unlocked) Color.White else Color(0xFF64748B), fontSize = 11.sp)
                                Text("${v.capacityTons.toInt()}t • +$${v.baseFreightRevenuePerSec.toInt()}/s", color = Color(0xFF34D399), fontSize = 11.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = routeName,
                        onValueChange = { routeName = it },
                        label = { Text("Nome da Rota", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                val cost = selectedVehicle.capacityTons * 800.0
                Button(
                    onClick = {
                        onAddRoute(selectedVehicle, routeName, origin, dest)
                        showNewRouteModal = false
                    },
                    enabled = state.playerCash >= cost,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
                ) {
                    Text("Criar Rota ($${cost.roundToInt()})")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewRouteModal = false }) {
                    Text("Cancelar", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}

@Composable
private fun RouteCardItem(route: FleetRoute) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (route.vehicleType) {
                            VehicleType.TRUCK -> Icons.Default.LocalShipping
                            VehicleType.FREIGHT_TRAIN -> Icons.Default.DirectionsRailway
                            VehicleType.CONTAINER_SHIP -> Icons.Default.DirectionsBoat
                            VehicleType.CARGO_JET -> Icons.Default.AirplanemodeActive
                            VehicleType.ORBITAL_ROCKET -> Icons.Default.RocketLaunch
                        },
                        contentDescription = null,
                        tint = Color(0xFF0D9488),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(route.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "+$${((route.vehicleType.baseFreightRevenuePerSec - route.vehicleType.fuelCostPerSec) * route.count).roundToInt()}/s",
                    color = Color(0xFF34D399),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = "${route.origin} ➔ ${route.destination} • Capacidade: ${route.vehicleType.capacityTons * route.count}t",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )

            LinearProgressIndicator(
                progress = { route.tripProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF0D9488),
                trackColor = Color(0xFF1E293B)
            )
        }
    }
}

@Composable
private fun VehicleTypeItem(vType: VehicleType, isUnlocked: Boolean) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color(0xFF0F172A) else Color(0xFF0B0F19)
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (vType) {
                        VehicleType.TRUCK -> Icons.Default.LocalShipping
                        VehicleType.FREIGHT_TRAIN -> Icons.Default.DirectionsRailway
                        VehicleType.CONTAINER_SHIP -> Icons.Default.DirectionsBoat
                        VehicleType.CARGO_JET -> Icons.Default.AirplanemodeActive
                        VehicleType.ORBITAL_ROCKET -> Icons.Default.RocketLaunch
                    },
                    contentDescription = null,
                    tint = if (isUnlocked) Color(0xFF0D9488) else Color(0xFF475569),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(vType.title, color = if (isUnlocked) Color.White else Color(0xFF64748B), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Capacidade: ${vType.capacityTons.toInt()}t • Velocidade: ${vType.speedKmh.toInt()} km/h", color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
            }

            if (!isUnlocked) {
                Surface(
                    color = Color(0xFF7F1D1D),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Era ${vType.unlockEra.stage}",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else {
                Text(
                    text = "+$${vType.baseFreightRevenuePerSec.toInt()}/s",
                    color = Color(0xFF34D399),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
