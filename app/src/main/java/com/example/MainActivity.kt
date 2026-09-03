package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BloombergTicker
import com.example.ui.components.EnterprisesScreen
import com.example.ui.components.EraProgressionDialog
import com.example.ui.components.FactoryGridScreen
import com.example.ui.components.FinancesDialog
import com.example.ui.components.LogisticsScreen
import com.example.ui.components.OrderBookScreen
import com.example.ui.components.P2PChatScreen
import com.example.ui.components.ResearchLabScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.MainTab
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TycoonApp()
            }
        }
    }
}

@Composable
fun TycoonApp(viewModel: GameViewModel = viewModel()) {
    val state by viewModel.gameState.collectAsState()
    var currentTab by remember { mutableStateOf(MainTab.FACTORY) }
    var showEraModal by remember { mutableStateOf(false) }
    var showFinancesModal by remember { mutableStateOf(false) }

    // Auto-dismiss notification after 4 seconds
    LaunchedEffect(state.lastNotification) {
        if (state.lastNotification != null) {
            delay(4000)
            viewModel.clearNotification()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF070B14),
        topBar = {
            BloombergTicker(
                state = state,
                onOpenFinances = { showFinancesModal = true },
                onOpenEraModal = { showEraModal = true }
            )
        },
        bottomBar = {
            TycoonBottomNavigation(
                currentTab = currentTab,
                onSelectTab = { currentTab = it },
                chatUnread = state.chatMessages.count { it.tradeOffer != null && !it.tradeOffer.isCompleted }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.FACTORY -> {
                    FactoryGridScreen(
                        state = state,
                        onUpgradePlot = { viewModel.upgradePlot(it) },
                        onAssignPlot = { idx, bId, mId -> viewModel.assignToPlot(idx, bId, mId) },
                        onQuickSell = { pId, qty -> viewModel.sellInstantMarket(pId, qty) },
                        onNavigateToEnterprises = { currentTab = MainTab.ENTERPRISES },
                        onNavigateToResearch = { currentTab = MainTab.RESEARCH_AI }
                    )
                }
                MainTab.RESEARCH_AI -> {
                    ResearchLabScreen(
                        state = state,
                        onInventMachine = { comps, prompt, onDone ->
                            viewModel.inventMachineOrProduct(comps, prompt, onDone)
                        },
                        onAssignToPlot = { idx, bId, mId ->
                            viewModel.assignToPlot(idx, bId, mId)
                        }
                    )
                }
                MainTab.ORDER_BOOK -> {
                    OrderBookScreen(
                        state = state,
                        onPostOrder = { pId, type, price, qty ->
                            viewModel.postMarketOrder(pId, type, price, qty)
                        },
                        onInstantBuy = { pId, qty -> viewModel.buyInstantMarket(pId, qty) },
                        onInstantSell = { pId, qty -> viewModel.sellInstantMarket(pId, qty) }
                    )
                }
                MainTab.LOGISTICS -> {
                    LogisticsScreen(
                        state = state,
                        onAddRoute = { type, name, orig, dest ->
                            viewModel.addFleetRoute(type, name, orig, dest)
                        }
                    )
                }
                MainTab.ENTERPRISES -> {
                    EnterprisesScreen(
                        state = state,
                        onBuyBusiness = { viewModel.buyBusiness(it) }
                    )
                }
                MainTab.CHAT_P2P -> {
                    P2PChatScreen(
                        state = state,
                        onSendMessage = { ch, text -> viewModel.sendChatMessage(ch, text) },
                        onAcceptOffer = { viewModel.acceptChatTradeOffer(it) },
                        onPostOffer = { ch, pId, qty, pr -> viewModel.postChatOffer(ch, pId, qty, pr) }
                    )
                }
            }

            // Top Flash Notification Banner
            state.lastNotification?.let { notif ->
                Surface(
                    color = Color(0xFF1E1B4B),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA78BFA)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .align(Alignment.TopCenter)
                        .testTag("notification_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = notif,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        IconButton(
                            onClick = { viewModel.clearNotification() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showEraModal) {
        EraProgressionDialog(
            state = state,
            onDismiss = { showEraModal = false }
        )
    }

    if (showFinancesModal) {
        FinancesDialog(
            state = state,
            onDismiss = { showFinancesModal = false }
        )
    }
}

@Composable
private fun TycoonBottomNavigation(
    currentTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    chatUnread: Int
) {
    NavigationBar(
        containerColor = Color(0xFF0F172A),
        contentColor = Color(0xFF38BDF8),
        tonalElevation = 8.dp
    ) {
        MainTab.values().forEach { tab ->
            val isSelected = currentTab == tab
            val iconVector: ImageVector = when (tab) {
                MainTab.FACTORY -> Icons.Default.Factory
                MainTab.RESEARCH_AI -> Icons.Default.Science
                MainTab.ORDER_BOOK -> Icons.Default.CandlestickChart
                MainTab.LOGISTICS -> Icons.Default.LocalShipping
                MainTab.ENTERPRISES -> Icons.Default.Domain
                MainTab.CHAT_P2P -> Icons.Default.Forum
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectTab(tab) },
                icon = {
                    if (tab == MainTab.CHAT_P2P && chatUnread > 0) {
                        BadgedBox(badge = { Badge { Text("$chatUnread") } }) {
                            Icon(iconVector, contentDescription = tab.title)
                        }
                    } else {
                        Icon(iconVector, contentDescription = tab.title)
                    }
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF38BDF8),
                    selectedTextColor = Color(0xFF38BDF8),
                    indicatorColor = Color(0xFF1E293B),
                    unselectedIconColor = Color(0xFF64748B),
                    unselectedTextColor = Color(0xFF64748B)
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}
