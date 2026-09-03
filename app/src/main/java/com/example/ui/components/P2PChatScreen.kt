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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.ChatChannel
import com.example.data.model.ChatMessage
import com.example.data.model.ProductCatalog
import com.example.data.model.TradeOfferCard
import com.example.data.repository.GameState
import kotlin.math.roundToInt

@Composable
fun P2PChatScreen(
    state: GameState,
    onSendMessage: (ChatChannel, String) -> Unit,
    onAcceptOffer: (TradeOfferCard) -> Unit,
    onPostOffer: (ChatChannel, String, Int, Double) -> Unit
) {
    var selectedChannel by remember { mutableStateOf(ChatChannel.NEGOTIATIONS) }
    var textInput by remember { mutableStateOf("") }
    var showOfferCreatorDialog by remember { mutableStateOf(false) }

    val filteredMessages = remember(state.chatMessages, selectedChannel) {
        state.chatMessages.filter { it.channel == selectedChannel }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(bottom = 76.dp)
    ) {
        // --- Channel Selector Tab Row ---
        ScrollableTabRow(
            selectedTabIndex = selectedChannel.ordinal,
            containerColor = Color(0xFF0F172A),
            contentColor = Color(0xFF38BDF8),
            edgePadding = 12.dp
        ) {
            ChatChannel.values().forEach { ch ->
                Tab(
                    selected = selectedChannel == ch,
                    onClick = { selectedChannel = ch },
                    text = {
                        Text(
                            text = "# ${ch.displayName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }

        // --- Channel Banner & MMO Status ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF131B2E))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF22C55E), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "1.420 Magnatas Online • Negociações P2P Ativas",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }

                Surface(
                    color = Color(0xFF7C3AED),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { showOfferCreatorDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalOffer, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Criar Oferta P2P", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- Chat Message Feed ---
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredMessages) { msg ->
                ChatMessageItem(
                    message = msg,
                    playerCorp = state.corporateName,
                    playerCash = state.playerCash,
                    onAcceptOffer = onAcceptOffer
                )
            }
        }

        // --- Input Bar ---
        Surface(
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            "Mensagem ou /oferta [id] [qtd] [preço]...",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            onSendMessage(selectedChannel, textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .background(Color(0xFF0284C7), CircleShape)
                        .size(42.dp)
                        .testTag("btn_send_chat")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    // --- Create Offer Dialog ---
    if (showOfferCreatorDialog) {
        var selectedItemToOffer by remember { mutableStateOf(state.inventory.keys.firstOrNull() ?: "p_01") }
        var offerQty by remember { mutableIntStateOf(10) }
        var offerPrice by remember {
            val p = state.marketProducts.find { it.id == selectedItemToOffer }
            mutableStateOf(p?.currentMarketPrice?.toString() ?: "20.0")
        }

        AlertDialog(
            onDismissRequest = { showOfferCreatorDialog = false },
            containerColor = Color(0xFF1E293B),
            title = {
                Text("Publicar Oferta Interativa P2P", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Selecione um item do seu estoque para negociar no chat:", color = Color(0xFF94A3B8), fontSize = 12.sp)

                    // Stock Item Picker
                    state.inventory.entries.take(5).forEach { (id, qty) ->
                        val prod = state.marketProducts.find { it.id == id } ?: ProductCatalog.findById(id)
                        if (prod != null) {
                            val isSel = selectedItemToOffer == id
                            Surface(
                                color = if (isSel) Color(0xFF0284C7) else Color(0xFF0F172A),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedItemToOffer = id }
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(prod.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Estoque: $qty un", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = offerQty.toString(),
                            onValueChange = { offerQty = it.toIntOrNull() ?: 1 },
                            label = { Text("Quantidade", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = offerPrice,
                            onValueChange = { offerPrice = it },
                            label = { Text("Preço Unit. ($)", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pr = offerPrice.toDoubleOrNull() ?: 10.0
                        onPostOffer(selectedChannel, selectedItemToOffer, offerQty, pr)
                        showOfferCreatorDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Publicar Card no Chat")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOfferCreatorDialog = false }) {
                    Text("Cancelar", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    playerCorp: String,
    playerCash: Double,
    onAcceptOffer: (TradeOfferCard) -> Unit
) {
    val isPlayer = message.senderCorp == playerCorp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isPlayer) Color(0xFF1E293B) else Color(0xFF0F172A),
                RoundedCornerShape(10.dp)
            )
            .border(
                1.dp,
                if (message.tradeOffer != null) Color(0xFF059669) else Color(0xFF1E293B),
                RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
    ) {
        // Sender Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = message.senderName,
                    color = if (isPlayer) Color(0xFF38BDF8) else Color(0xFFA78BFA),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = message.senderCorp,
                        color = Color(0xFF94A3B8),
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Message body
        Text(
            text = message.text,
            color = Color(0xFFE2E8F0),
            fontSize = 12.sp
        )

        // Interactive Trade Offer Card
        message.tradeOffer?.let { offer ->
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color(0xFF064E3B).copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Handshake,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${offer.quantity}x ${offer.productName}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "$${String.format("%.2f", offer.unitPrice)}/un • Total: $${String.format("%,.0f", offer.totalValue)}",
                            color = Color(0xFF34D399),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (offer.isCompleted) {
                        Surface(
                            color = Color(0xFF047857),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Liquidado", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (offer.sellerName != playerCorp) {
                        Button(
                            onClick = { onAcceptOffer(offer) },
                            enabled = playerCash >= offer.totalValue,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Comprar Agora", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = "Sua Oferta Ativa",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
