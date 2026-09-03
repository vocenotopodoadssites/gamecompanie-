package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatChannel
import com.example.data.model.CustomMachine
import com.example.data.model.OrderType
import com.example.data.model.Product
import com.example.data.model.TradeOfferCard
import com.example.data.model.VehicleType
import com.example.data.repository.GameRepository
import com.example.data.repository.GameState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val icon: String) {
    FACTORY("Fábrica", "factory"),
    RESEARCH_AI("P&D IA", "science"),
    ORDER_BOOK("Bolsa & Ordens", "candlestick"),
    LOGISTICS("Logística", "local_shipping"),
    ENTERPRISES("50 Negócios", "domain"),
    CHAT_P2P("Chat P2P", "forum")
}

class GameViewModel(
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    val gameState: StateFlow<GameState> = repository.gameState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GameState())

    init {
        // Start game simulation loop
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                repository.onGameTick()
            }
        }
    }

    fun buyBusiness(businessId: String): Boolean = repository.buyBusiness(businessId)

    fun upgradePlot(plotIndex: Int): Boolean = repository.upgradePlot(plotIndex)

    fun assignToPlot(plotIndex: Int, businessId: String?, customMachineId: String?) =
        repository.assignToPlot(plotIndex, businessId, customMachineId)

    fun sellInstantMarket(productId: String, quantity: Int): Boolean =
        repository.sellInstantMarket(productId, quantity)

    fun buyInstantMarket(productId: String, quantity: Int): Boolean =
        repository.buyInstantMarket(productId, quantity)

    fun postMarketOrder(productId: String, type: OrderType, unitPrice: Double, quantity: Int): Boolean =
        repository.postMarketOrder(productId, type, unitPrice, quantity)

    fun acceptChatTradeOffer(offer: TradeOfferCard): Boolean =
        repository.acceptChatTradeOffer(offer)

    fun postChatOffer(channel: ChatChannel, productId: String, quantity: Int, unitPrice: Double): Boolean =
        repository.postChatOffer(channel, productId, quantity, unitPrice)

    fun sendChatMessage(channel: ChatChannel, text: String) =
        repository.sendChatMessage(channel, text)

    fun addFleetRoute(vehicleType: VehicleType, name: String, origin: String, destination: String): Boolean =
        repository.addFleetRoute(vehicleType, name, origin, destination)

    fun inventMachineOrProduct(
        selectedComponents: List<Product>,
        userPrompt: String,
        onComplete: (CustomMachine) -> Unit = {}
    ) {
        repository.inventMachineOrProduct(viewModelScope, selectedComponents, userPrompt, onComplete)
    }

    fun clearNotification() = repository.clearNotification()
}
