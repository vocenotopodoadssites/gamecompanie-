package com.example.data.repository

import com.example.ai.GeminiInventionService
import com.example.data.model.BusinessCatalog
import com.example.data.model.BusinessType
import com.example.data.model.ChatChannel
import com.example.data.model.ChatMessage
import com.example.data.model.CustomMachine
import com.example.data.model.FactoryPlot
import com.example.data.model.FleetRoute
import com.example.data.model.GameEra
import com.example.data.model.MarketOrder
import com.example.data.model.OrderType
import com.example.data.model.Product
import com.example.data.model.ProductCatalog
import com.example.data.model.SectorType
import com.example.data.model.TradeExecution
import com.example.data.model.TradeOfferCard
import com.example.data.model.VehicleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

data class GameState(
    val corporateName: String = "Apex Nexus Corp",
    val playerCash: Double = 8500.0,
    val currentEra: GameEra = GameEra.SUBSISTENCE_FARM,
    val inventory: Map<String, Int> = mapOf(
        "p_01" to 20, // Milho
        "p_02" to 15, // Soja
        "p_15" to 40, // Água
        "p_11" to 10, // Minério de Ferro
        "p_28" to 5,  // Lingote de ferro
        "p_31" to 8,  // Parafusos
        "p_36" to 2   // Chassi
    ),
    val ownedBusinesses: Map<String, Int> = mapOf(
        "biz_01" to 1 // 1x Fazenda Agrícola Geral
    ),
    val customMachines: List<CustomMachine> = emptyList(),
    val dynamicProducts: List<Product> = emptyList(),
    val factoryPlots: List<FactoryPlot> = (0 until 12).map { index ->
        if (index == 0) FactoryPlot(plotIndex = 0, businessId = "biz_01", level = 1, isRunning = true)
        else FactoryPlot(plotIndex = index)
    },
    val marketProducts: List<Product> = ProductCatalog.BASE_PRODUCTS,
    val orderBook: List<MarketOrder> = emptyList(),
    val tradeHistory: List<TradeExecution> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val fleetRoutes: List<FleetRoute> = listOf(
        FleetRoute(
            routeId = "route_1",
            name = "Rota Agrícola da Capital",
            vehicleType = VehicleType.TRUCK,
            count = 2,
            origin = "Silos da Fazenda",
            destination = "Mercado Central",
            activeCargoProductId = "p_01",
            isActive = true
        )
    ),
    val totalEnergyGeneratedKw: Double = 15.0,
    val totalEnergyConsumedKw: Double = 6.0,
    val taxRatePct: Double = 5.0,
    val netWorth: Double = 12000.0,
    val tickerMessages: List<String> = listOf(
        "BOMBA: Alta procura por semicondutores eleva cotação de Sílica em +8.4%",
        "NOVO CONTRATO: Fretamento de 50t de Milho liquidado com sucesso a $14.20/un",
        "ENERGIA: Matriz limpa atinge estabilidade nominal com geração excedente",
        "BOLSA P2P: Livro de ofertas registra volume recorde de $420.000 no dia"
    ),
    val isAiInventing: Boolean = false,
    val lastNotification: String? = null
)

class GameRepository(
    private val inventionService: GeminiInventionService = GeminiInventionService()
) {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val simulatedCompetitors = listOf(
        "OmniCorp Industries", "Titan Logistics", "Solaris Energy",
        "BioSynth Brasil", "AeroVanguard SA", "Orbital Prime",
        "AgroVale S.A.", "Cyberdyne Heavy Metals", "Quantum Foundry"
    )

    init {
        seedInitialMarketOrders()
        seedInitialChat()
    }

    private fun seedInitialMarketOrders() {
        val orders = mutableListOf<MarketOrder>()
        val popularIds = listOf("p_01", "p_02", "p_11", "p_14", "p_16", "p_28", "p_36", "p_38")
        popularIds.forEach { prodId ->
            val product = ProductCatalog.findById(prodId) ?: return@forEach
            // 2 Bids (Buy orders)
            orders.add(
                MarketOrder(
                    orderId = UUID.randomUUID().toString(),
                    productId = prodId,
                    productName = product.name,
                    orderType = OrderType.BUY,
                    unitPrice = (product.basePrice * (0.92 + Random.nextDouble(0.06))).roundToDecimals(2),
                    quantity = Random.nextInt(15, 60),
                    issuerName = simulatedCompetitors.random()
                )
            )
            // 2 Asks (Sell orders)
            orders.add(
                MarketOrder(
                    orderId = UUID.randomUUID().toString(),
                    productId = prodId,
                    productName = product.name,
                    orderType = OrderType.SELL,
                    unitPrice = (product.basePrice * (1.03 + Random.nextDouble(0.08))).roundToDecimals(2),
                    quantity = Random.nextInt(10, 45),
                    issuerName = simulatedCompetitors.random()
                )
            )
        }
        _gameState.value = _gameState.value.copy(orderBook = orders)
    }

    private fun seedInitialChat() {
        val initialMessages = listOf(
            ChatMessage(
                messageId = "msg_1",
                senderName = "Carlos_Trader",
                senderCorp = "OmniCorp Industries",
                channel = ChatChannel.GLOBAL,
                text = "Bom dia magnatas! Compro qualquer volume de Milho e Soja para minhas moendas.",
                timestamp = System.currentTimeMillis() - 60000
            ),
            ChatMessage(
                messageId = "msg_2",
                senderName = "Dra_Elena_Voz",
                senderCorp = "Quantum Foundry",
                channel = ChatChannel.NEGOTIATIONS,
                text = "Despachando oferta relâmpago de lingotes refinados:",
                tradeOffer = TradeOfferCard(
                    offerId = "card_init_1",
                    productId = "p_28",
                    productName = "Lingote de Ferro",
                    quantity = 25,
                    unitPrice = 44.0,
                    sellerName = "Dra_Elena_Voz"
                ),
                timestamp = System.currentTimeMillis() - 40000
            ),
            ChatMessage(
                messageId = "msg_3",
                senderName = "Diretoria_Logistica",
                senderCorp = "Titan Logistics",
                channel = ChatChannel.CORPORATIONS,
                text = "Todas as rotas rodoviárias estão com taxa de segurança de 0%. Aproveitem o frete!",
                timestamp = System.currentTimeMillis() - 20000
            )
        )
        _gameState.value = _gameState.value.copy(chatMessages = initialMessages)
    }

    // --- Core Game Simulation Tick (runs every second) ---
    fun onGameTick() {
        val current = _gameState.value
        val newInv = current.inventory.toMutableMap()
        var cashDelta = 0.0
        var totalEnergyCons = 0.0
        var totalEnergyGen = 0.0

        // 1. Process Factory Plots production
        val updatedPlots = current.factoryPlots.map { plot ->
            if (!plot.isRunning) return@map plot

            if (plot.businessId != null) {
                val biz = BusinessCatalog.findById(plot.businessId)
                if (biz != null) {
                    totalEnergyCons += biz.recipe.energyCostKw * plot.level
                    if (biz.recipe.outputItemId == "p_16") {
                        totalEnergyGen += (biz.recipe.outputQuantity * 5.0 * plot.level)
                    }

                    // Revenue for tertiary service businesses
                    if (biz.revenuePerSecond > 0) {
                        cashDelta += biz.revenuePerSecond * plot.level
                    }

                    // Production cycle
                    val newProgress = plot.progress + (1.0f / max(1, biz.recipe.cycleTimeSeconds))
                    if (newProgress >= 1.0f) {
                        // Check inputs
                        var canProduce = true
                        for ((inId, inQty) in biz.recipe.inputItemIds) {
                            val available = newInv[inId] ?: 0
                            if (available < inQty * plot.level) {
                                canProduce = false
                                break
                            }
                        }

                        if (canProduce) {
                            biz.recipe.inputItemIds.forEach { (inId, inQty) ->
                                newInv[inId] = (newInv[inId] ?: 0) - (inQty * plot.level)
                            }
                            val producedQty = biz.recipe.outputQuantity * plot.level
                            if (producedQty > 0) {
                                newInv[biz.recipe.outputItemId] = (newInv[biz.recipe.outputItemId] ?: 0) + producedQty
                            }
                            return@map plot.copy(progress = 0f, lastProducedQty = producedQty)
                        } else {
                            return@map plot.copy(progress = 0.99f)
                        }
                    } else {
                        return@map plot.copy(progress = newProgress)
                    }
                }
            } else if (plot.customMachineId != null) {
                val machine = current.customMachines.find { it.id == plot.customMachineId }
                if (machine != null) {
                    totalEnergyCons += machine.powerConsumptionKw * plot.level
                    val newProgress = plot.progress + (0.25f * machine.productionMultiplier.toFloat())
                    if (newProgress >= 1.0f) {
                        val producedQty = (2 * machine.productionMultiplier * plot.level).roundToInt()
                        newInv[machine.outputProductId] = (newInv[machine.outputProductId] ?: 0) + producedQty
                        return@map plot.copy(progress = 0f, lastProducedQty = producedQty)
                    } else {
                        return@map plot.copy(progress = newProgress)
                    }
                }
            }
            plot
        }

        // 2. Process Fleet Routes
        val updatedRoutes = current.fleetRoutes.map { route ->
            if (!route.isActive) return@map route
            val rev = (route.vehicleType.baseFreightRevenuePerSec * route.count) - (route.vehicleType.fuelCostPerSec * route.count)
            cashDelta += max(0.0, rev)
            val newProg = (route.tripProgress + 0.05f) % 1.0f
            route.copy(tripProgress = newProg)
        }

        // 3. Fluctuate Market Prices slightly & Order Book simulation
        val updatedMarket = current.marketProducts.map { prod ->
            val drift = (Random.nextDouble(-0.02, 0.025))
            val newPrice = max(prod.basePrice * 0.4, prod.currentMarketPrice * (1.0 + drift)).roundToDecimals(2)
            val pct = (((newPrice - prod.basePrice) / prod.basePrice) * 100.0).roundToDecimals(1)
            prod.copy(currentMarketPrice = newPrice, priceChangePct = pct)
        }

        // 4. Random simulated trade execution or MMO order match
        val currentOrders = current.orderBook.toMutableList()
        val newTrades = current.tradeHistory.toMutableList()
        if (Random.nextFloat() < 0.35 && currentOrders.isNotEmpty()) {
            val randomOrderIndex = currentOrders.indices.random()
            val order = currentOrders[randomOrderIndex]
            val matchedQty = min(order.quantity - order.filledQuantity, Random.nextInt(3, 15))
            if (matchedQty > 0) {
                val updatedOrder = order.copy(filledQuantity = order.filledQuantity + matchedQty)
                if (updatedOrder.filledQuantity >= updatedOrder.quantity) {
                    currentOrders.removeAt(randomOrderIndex)
                } else {
                    currentOrders[randomOrderIndex] = updatedOrder
                }
                newTrades.add(
                    0,
                    TradeExecution(
                        tradeId = UUID.randomUUID().toString().take(6),
                        productId = order.productId,
                        productName = order.productName,
                        unitPrice = order.unitPrice,
                        quantity = matchedQty,
                        buyerName = if (order.orderType == OrderType.SELL) simulatedCompetitors.random() else order.issuerName,
                        sellerName = if (order.orderType == OrderType.BUY) simulatedCompetitors.random() else order.issuerName
                    )
                )
                if (newTrades.size > 20) newTrades.removeLast()
            }
        }

        // 5. Occasionally inject a dynamic MMO trader offer into Chat
        val currentChat = current.chatMessages.toMutableList()
        if (Random.nextFloat() < 0.12 && currentChat.size < 40) {
            val randomProd = current.marketProducts.random()
            val qty = Random.nextInt(10, 50)
            val discount = Random.nextDouble(0.85, 1.05)
            val unitPrice = (randomProd.currentMarketPrice * discount).roundToDecimals(2)
            val trader = simulatedCompetitors.random()
            currentChat.add(
                ChatMessage(
                    messageId = UUID.randomUUID().toString(),
                    senderName = trader,
                    senderCorp = "$trader Ltd",
                    channel = ChatChannel.NEGOTIATIONS,
                    text = "OFERTA SPOT P2P: Lote excedente de ${randomProd.name} disponível para despacho imediato!",
                    tradeOffer = TradeOfferCard(
                        offerId = UUID.randomUUID().toString(),
                        productId = randomProd.id,
                        productName = randomProd.name,
                        quantity = qty,
                        unitPrice = unitPrice,
                        sellerName = trader
                    )
                )
            )
        }

        // 6. Calculate Net Worth
        val inventoryValue = newInv.entries.sumOf { (id, qty) ->
            val pr = updatedMarket.find { it.id == id } ?: ProductCatalog.findById(id)
            (pr?.currentMarketPrice ?: 10.0) * qty
        }
        val businessValue = current.ownedBusinesses.entries.sumOf { (id, count) ->
            val b = BusinessCatalog.findById(id)
            (b?.baseCost ?: 1000.0) * count
        }
        val newCash = current.playerCash + cashDelta
        val netWorth = newCash + inventoryValue + businessValue

        // 7. Check Era Progression eligibility
        val calculatedEra = when {
            netWorth >= GameEra.SPACE_CORPORATION.requiredNetWorth -> GameEra.SPACE_CORPORATION
            netWorth >= GameEra.TECH_LOGISTICS_CONGLOMERATE.requiredNetWorth -> GameEra.TECH_LOGISTICS_CONGLOMERATE
            netWorth >= GameEra.URBAN_INDUSTRIAL_HUB.requiredNetWorth -> GameEra.URBAN_INDUSTRIAL_HUB
            netWorth >= GameEra.LOCAL_AGROINDUSTRY.requiredNetWorth -> GameEra.LOCAL_AGROINDUSTRY
            else -> GameEra.SUBSISTENCE_FARM
        }

        val finalEra = if (calculatedEra.stage > current.currentEra.stage) calculatedEra else current.currentEra

        _gameState.value = current.copy(
            playerCash = newCash,
            inventory = newInv,
            factoryPlots = updatedPlots,
            fleetRoutes = updatedRoutes,
            marketProducts = updatedMarket,
            orderBook = currentOrders,
            tradeHistory = newTrades,
            chatMessages = currentChat,
            totalEnergyConsumedKw = (totalEnergyCons * 10).roundToInt() / 10.0,
            totalEnergyGeneratedKw = max(15.0, (totalEnergyGen * 10).roundToInt() / 10.0),
            netWorth = netWorth,
            currentEra = finalEra
        )
    }

    // --- User Actions ---

    fun buyBusiness(businessId: String): Boolean {
        val biz = BusinessCatalog.findById(businessId) ?: return false
        val current = _gameState.value
        if (current.playerCash < biz.baseCost) return false

        val newCash = current.playerCash - biz.baseCost
        val owned = current.ownedBusinesses.toMutableMap()
        owned[businessId] = (owned[businessId] ?: 0) + 1

        // Auto-assign to first empty plot if available
        val plots = current.factoryPlots.toMutableList()
        val emptyIndex = plots.indexOfFirst { it.businessId == null && it.customMachineId == null }
        if (emptyIndex != -1) {
            plots[emptyIndex] = FactoryPlot(plotIndex = emptyIndex, businessId = businessId, level = 1, isRunning = true)
        }

        _gameState.value = current.copy(
            playerCash = newCash,
            ownedBusinesses = owned,
            factoryPlots = plots,
            lastNotification = "Empresa '${biz.name}' adquirida e integrada à cadeia corporativa!"
        )
        return true
    }

    fun upgradePlot(plotIndex: Int): Boolean {
        val current = _gameState.value
        val plot = current.factoryPlots.getOrNull(plotIndex) ?: return false
        val cost = 2500.0 * (plot.level * 1.5)
        if (current.playerCash < cost) return false

        val updatedPlots = current.factoryPlots.toMutableList()
        updatedPlots[plotIndex] = plot.copy(level = plot.level + 1)
        _gameState.value = current.copy(
            playerCash = current.playerCash - cost,
            factoryPlots = updatedPlots,
            lastNotification = "Setor #${plotIndex + 1} aprimorado para Nível ${plot.level + 1}!"
        )
        return true
    }

    fun assignToPlot(plotIndex: Int, businessId: String?, customMachineId: String?) {
        val current = _gameState.value
        val plots = current.factoryPlots.toMutableList()
        if (plotIndex in plots.indices) {
            plots[plotIndex] = FactoryPlot(
                plotIndex = plotIndex,
                businessId = businessId,
                customMachineId = customMachineId,
                level = 1,
                isRunning = true
            )
            _gameState.value = current.copy(
                factoryPlots = plots,
                lastNotification = "Unidade instalada no Lote #${plotIndex + 1} com sucesso!"
            )
        }
    }

    fun sellInstantMarket(productId: String, quantity: Int): Boolean {
        val current = _gameState.value
        val available = current.inventory[productId] ?: 0
        if (available < quantity) return false

        val prod = current.marketProducts.find { it.id == productId } ?: ProductCatalog.findById(productId) ?: return false
        val revenue = (prod.currentMarketPrice * quantity * (1.0 - (current.taxRatePct / 100.0)))
        val newInv = current.inventory.toMutableMap()
        newInv[productId] = available - quantity
        if (newInv[productId] == 0) newInv.remove(productId)

        _gameState.value = current.copy(
            playerCash = current.playerCash + revenue,
            inventory = newInv,
            lastNotification = "Venda liquidada: $quantity un de ${prod.name} por +$${revenue.roundToInt()}"
        )
        return true
    }

    fun buyInstantMarket(productId: String, quantity: Int): Boolean {
        val current = _gameState.value
        val prod = current.marketProducts.find { it.id == productId } ?: ProductCatalog.findById(productId) ?: return false
        val cost = prod.currentMarketPrice * quantity
        if (current.playerCash < cost) return false

        val newInv = current.inventory.toMutableMap()
        newInv[productId] = (newInv[productId] ?: 0) + quantity

        _gameState.value = current.copy(
            playerCash = current.playerCash - cost,
            inventory = newInv,
            lastNotification = "Compra efetuada: $quantity un de ${prod.name} por -$${cost.roundToInt()}"
        )
        return true
    }

    fun postMarketOrder(productId: String, type: OrderType, unitPrice: Double, quantity: Int): Boolean {
        val current = _gameState.value
        val prod = current.marketProducts.find { it.id == productId } ?: ProductCatalog.findById(productId) ?: return false

        if (type == OrderType.SELL) {
            val available = current.inventory[productId] ?: 0
            if (available < quantity) return false
            val newInv = current.inventory.toMutableMap()
            newInv[productId] = available - quantity
            if (newInv[productId] == 0) newInv.remove(productId)

            val newOrder = MarketOrder(
                orderId = UUID.randomUUID().toString(),
                productId = productId,
                productName = prod.name,
                orderType = OrderType.SELL,
                unitPrice = unitPrice,
                quantity = quantity,
                issuerName = current.corporateName,
                isPlayerOrder = true
            )
            _gameState.value = current.copy(
                inventory = newInv,
                orderBook = listOf(newOrder) + current.orderBook,
                lastNotification = "Ordem de VENDA registrada no Livro: $quantity x ${prod.name} a $$unitPrice"
            )
            return true
        } else {
            val totalCost = unitPrice * quantity
            if (current.playerCash < totalCost) return false
            val newOrder = MarketOrder(
                orderId = UUID.randomUUID().toString(),
                productId = productId,
                productName = prod.name,
                orderType = OrderType.BUY,
                unitPrice = unitPrice,
                quantity = quantity,
                issuerName = current.corporateName,
                isPlayerOrder = true
            )
            _gameState.value = current.copy(
                playerCash = current.playerCash - totalCost,
                orderBook = listOf(newOrder) + current.orderBook,
                lastNotification = "Ordem de COMPRA registrada no Livro: $quantity x ${prod.name} a $$unitPrice"
            )
            return true
        }
    }

    fun acceptChatTradeOffer(offer: TradeOfferCard): Boolean {
        val current = _gameState.value
        if (current.playerCash < offer.totalValue) return false

        val newCash = current.playerCash - offer.totalValue
        val newInv = current.inventory.toMutableMap()
        newInv[offer.productId] = (newInv[offer.productId] ?: 0) + offer.quantity

        val updatedMessages = current.chatMessages.map { msg ->
            if (msg.tradeOffer?.offerId == offer.offerId) {
                msg.copy(tradeOffer = offer.copy(isCompleted = true))
            } else msg
        }

        val completedTrade = TradeExecution(
            tradeId = UUID.randomUUID().toString().take(6),
            productId = offer.productId,
            productName = offer.productName,
            unitPrice = offer.unitPrice,
            quantity = offer.quantity,
            buyerName = current.corporateName,
            sellerName = offer.sellerName
        )

        _gameState.value = current.copy(
            playerCash = newCash,
            inventory = newInv,
            chatMessages = updatedMessages,
            tradeHistory = listOf(completedTrade) + current.tradeHistory,
            lastNotification = "CONTRATO EXECUTADO! Recebido ${offer.quantity} un de ${offer.productName} de ${offer.sellerName}"
        )
        return true
    }

    fun postChatOffer(channel: ChatChannel, productId: String, quantity: Int, unitPrice: Double): Boolean {
        val current = _gameState.value
        val available = current.inventory[productId] ?: 0
        if (available < quantity) return false

        val prod = current.marketProducts.find { it.id == productId } ?: ProductCatalog.findById(productId) ?: return false
        val newInv = current.inventory.toMutableMap()
        newInv[productId] = available - quantity
        if (newInv[productId] == 0) newInv.remove(productId)

        val offerCard = TradeOfferCard(
            offerId = UUID.randomUUID().toString(),
            productId = productId,
            productName = prod.name,
            quantity = quantity,
            unitPrice = unitPrice,
            sellerName = current.corporateName
        )

        val chatMsg = ChatMessage(
            messageId = UUID.randomUUID().toString(),
            senderName = current.corporateName,
            senderCorp = current.corporateName,
            channel = channel,
            text = "OFERTA P2P CORPORATIVA: Lote aberto para negociação no chat!",
            tradeOffer = offerCard
        )

        _gameState.value = current.copy(
            inventory = newInv,
            chatMessages = current.chatMessages + chatMsg,
            lastNotification = "Card interativo de oferta publicado no chat com sucesso!"
        )
        return true
    }

    fun sendChatMessage(channel: ChatChannel, text: String) {
        val current = _gameState.value
        // Check for command /oferta [id] [qtd] [preco]
        if (text.startsWith("/oferta")) {
            val parts = text.split(" ").filter { it.isNotBlank() }
            if (parts.size >= 4) {
                val pId = parts[1]
                val qty = parts[2].toIntOrNull() ?: 10
                val price = parts[3].toDoubleOrNull() ?: 20.0
                postChatOffer(channel, pId, qty, price)
                return
            }
        }

        val msg = ChatMessage(
            messageId = UUID.randomUUID().toString(),
            senderName = current.corporateName,
            senderCorp = current.corporateName,
            channel = channel,
            text = text
        )
        _gameState.value = current.copy(chatMessages = current.chatMessages + msg)
    }

    fun addFleetRoute(vehicleType: VehicleType, name: String, origin: String, destination: String): Boolean {
        val current = _gameState.value
        val cost = vehicleType.capacityTons * 800.0
        if (current.playerCash < cost) return false

        val route = FleetRoute(
            routeId = UUID.randomUUID().toString(),
            name = name,
            vehicleType = vehicleType,
            count = 1,
            origin = origin,
            destination = destination,
            isActive = true
        )

        _gameState.value = current.copy(
            playerCash = current.playerCash - cost,
            fleetRoutes = current.fleetRoutes + route,
            lastNotification = "Nova rota logística criada: $name (${vehicleType.title})"
        )
        return true
    }

    // --- Dynamic AI Invention ---
    fun inventMachineOrProduct(
        scope: CoroutineScope,
        selectedComponents: List<Product>,
        userPrompt: String,
        onComplete: (CustomMachine) -> Unit
    ) {
        _gameState.value = _gameState.value.copy(isAiInventing = true)
        scope.launch {
            try {
                val (machine, product) = inventionService.generateInvention(selectedComponents, userPrompt)
                val current = _gameState.value
                val newMachines = current.customMachines + machine
                val newDynamicProducts = current.dynamicProducts + product
                val newMarket = current.marketProducts + product

                // Consume 1 of each selected component from player inventory
                val newInv = current.inventory.toMutableMap()
                selectedComponents.forEach { comp ->
                    val curQty = newInv[comp.id] ?: 0
                    if (curQty > 0) {
                        newInv[comp.id] = curQty - 1
                        if (newInv[comp.id] == 0) newInv.remove(comp.id)
                    }
                }

                _gameState.value = current.copy(
                    isAiInventing = false,
                    customMachines = newMachines,
                    dynamicProducts = newDynamicProducts,
                    marketProducts = newMarket,
                    inventory = newInv,
                    lastNotification = "IA PROJETOU: '${machine.name}' gerado com sucesso!"
                )
                onComplete(machine)
            } catch (e: Exception) {
                _gameState.value = _gameState.value.copy(
                    isAiInventing = false,
                    lastNotification = "Falha ao sintetizar: ${e.message}"
                )
            }
        }
    }

    fun clearNotification() {
        _gameState.value = _gameState.value.copy(lastNotification = null)
    }

    private fun Double.roundToDecimals(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}
