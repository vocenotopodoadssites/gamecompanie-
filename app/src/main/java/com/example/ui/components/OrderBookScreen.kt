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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketOrder
import com.example.data.model.OrderType
import com.example.data.model.Product
import com.example.data.model.ProductCatalog
import com.example.data.repository.GameState
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun OrderBookScreen(
    state: GameState,
    onPostOrder: (String, OrderType, Double, Int) -> Unit,
    onInstantBuy: (String, Int) -> Unit,
    onInstantSell: (String, Int) -> Unit
) {
    var selectedProductId by remember { mutableStateOf("p_01") } // default Milho
    var orderTypeTab by remember { mutableIntStateOf(0) } // 0 = Buy, 1 = Sell
    var orderPrice by remember { mutableDoubleStateOf(14.0) }
    var orderQuantity by remember { mutableIntStateOf(10) }
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0 = Livro, 1 = Histórico

    val selectedProduct = state.marketProducts.find { it.id == selectedProductId }
        ?: ProductCatalog.findById(selectedProductId) ?: state.marketProducts.first()

    val bids = remember(state.orderBook, selectedProductId) {
        state.orderBook.filter { it.productId == selectedProductId && it.orderType == OrderType.BUY }
            .sortedByDescending { it.unitPrice }
    }
    val asks = remember(state.orderBook, selectedProductId) {
        state.orderBook.filter { it.productId == selectedProductId && it.orderType == OrderType.SELL }
            .sortedBy { it.unitPrice }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(bottom = 80.dp),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Commodity Selector Ribbon ---
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                val featuredIds = listOf("p_01", "p_02", "p_11", "p_14", "p_16", "p_26", "p_28", "p_36", "p_38", "p_52", "p_72")
                items(featuredIds) { fId ->
                    val prod = state.marketProducts.find { it.id == fId } ?: ProductCatalog.findById(fId)
                    if (prod != null) {
                        val isSelected = prod.id == selectedProductId
                        Surface(
                            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B)
                            ),
                            modifier = Modifier.clickable {
                                selectedProductId = prod.id
                                orderPrice = prod.currentMarketPrice
                            }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                Text(
                                    text = prod.name.split(" ").first(),
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$${String.format("%.1f", prod.currentMarketPrice)}",
                                    color = if (prod.priceChangePct >= 0) Color(0xFF34D399) else Color(0xFFF87171),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Current Asset Banner Card ---
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF131B2E)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedProduct.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedProduct.category.displayName} • ${selectedProduct.physicalState.label}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${String.format("%.2f", selectedProduct.currentMarketPrice)}",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isUp = selectedProduct.priceChangePct >= 0
                            Icon(
                                imageVector = if (isUp) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (isUp) Color(0xFF34D399) else Color(0xFFF87171),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${if (isUp) "+" else ""}${selectedProduct.priceChangePct}%",
                                color = if (isUp) Color(0xFF34D399) else Color(0xFFF87171),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // --- Sub Tabs: Livro de Ordens vs Histórico ---
        item {
            TabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = Color(0xFF0F172A),
                contentColor = Color(0xFF38BDF8)
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = { Text("Profundidade de Livro", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = { Text("Histórico da Bolsa", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedSubTab == 0) {
            // --- Live Order Book (Bids vs Asks) ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ORDENS DE COMPRA (BIDS)", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Black)
                            Text("ORDENS DE VENDA (ASKS)", color = Color(0xFFF87171), fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Bids column (left)
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (bids.isEmpty()) {
                                    Text("Sem ofertas de compra", color = Color(0xFF475569), fontSize = 10.sp)
                                } else {
                                    bids.take(5).forEach { b ->
                                        OrderRowItem(
                                            price = b.unitPrice,
                                            qty = b.quantity - b.filledQuantity,
                                            issuer = b.issuerName,
                                            isBid = true
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Asks column (right)
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (asks.isEmpty()) {
                                    Text("Sem ofertas de venda", color = Color(0xFF475569), fontSize = 10.sp)
                                } else {
                                    asks.take(5).forEach { a ->
                                        OrderRowItem(
                                            price = a.unitPrice,
                                            qty = a.quantity - a.filledQuantity,
                                            issuer = a.issuerName,
                                            isBid = false
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // --- Trade Executions Stream ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "ÚLTIMAS EXECUÇÕES P2P NO SERVIDOR",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        state.tradeHistory.take(8).forEach { trade ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${trade.quantity}x ${trade.productName}",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${trade.sellerName} ➔ ${trade.buyerName}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 9.sp
                                    )
                                }
                                Text(
                                    text = "$${String.format("%.2f", trade.unitPrice)}/un",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Action Box: Instant Execution & Limit Orders ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "TERMINAL DE EXECUÇÃO DE ORDENS",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )

                    // Quick Market Buy / Sell
                    val playerItemQty = state.inventory[selectedProductId] ?: 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onInstantBuy(selectedProductId, orderQuantity) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_market_buy")
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Comprar $orderQuantity un", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onInstantSell(selectedProductId, orderQuantity) },
                            enabled = playerItemQty >= orderQuantity,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC2626),
                                disabledContainerColor = Color(0xFF374151)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_market_sell")
                        ) {
                            Icon(Icons.Default.Sell, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Vender $orderQuantity un", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Limit Order Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quantity Adjuster
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Qtd:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { orderQuantity = max(1, orderQuantity - 5) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                text = "$orderQuantity",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(
                                onClick = { orderQuantity += 5 },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }

                        // Limit Price Input
                        OutlinedTextField(
                            value = orderPrice.toString(),
                            onValueChange = { str ->
                                str.toDoubleOrNull()?.let { orderPrice = it }
                            },
                            label = { Text("Preço Limite ($)", color = Color(0xFF94A3B8), fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    // Register Order on Order Book
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onPostOrder(selectedProductId, OrderType.BUY, orderPrice, orderQuantity) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Registrar Compra Limite", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onPostOrder(selectedProductId, OrderType.SELL, orderPrice, orderQuantity) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Registrar Venda Limite", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderRowItem(
    price: Double,
    qty: Int,
    issuer: String,
    isBid: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isBid) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFF7F1D1D).copy(alpha = 0.5f),
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$${String.format("%.2f", price)}",
                    color = if (isBid) Color(0xFF34D399) else Color(0xFFF87171),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = issuer.take(12),
                    color = Color(0xFF94A3B8),
                    fontSize = 8.sp
                )
            }
            Text(
                text = "${qty}un",
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun IconButton(onClick: () -> Unit, modifier: Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier.clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
