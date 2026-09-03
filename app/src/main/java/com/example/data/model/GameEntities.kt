package com.example.data.model

data class CustomMachine(
    val id: String,
    val name: String,
    val category: String, // "Maquinário Híbrido" or "Produto Especial"
    val promptUsed: String,
    val componentNames: List<String>,
    val powerConsumptionKw: Double,
    val productionMultiplier: Double,
    val durabilityHrs: Int,
    val weightKg: Double,
    val estimatedValue: Double,
    val physicalState: PhysicalState,
    val outputProductId: String,
    val primaryColorHex: Long = 0xFF3B82F6,
    val secondaryColorHex: Long = 0xFF10B981,
    val iconShape: String = "GEAR_HYBRID",
    val technicalSummary: String
)

data class FactoryPlot(
    val plotIndex: Int,
    val businessId: String? = null,
    val customMachineId: String? = null,
    val level: Int = 1,
    val isRunning: Boolean = true,
    val progress: Float = 0f,
    val lastProducedQty: Int = 0
)

enum class OrderType { BUY, SELL }

data class MarketOrder(
    val orderId: String,
    val productId: String,
    val productName: String,
    val orderType: OrderType,
    val unitPrice: Double,
    val quantity: Int,
    val filledQuantity: Int = 0,
    val issuerName: String,
    val isPlayerOrder: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class TradeExecution(
    val tradeId: String,
    val productId: String,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val buyerName: String,
    val sellerName: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class TradeOfferCard(
    val offerId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalValue: Double = quantity * unitPrice,
    val sellerName: String,
    val isCompleted: Boolean = false
)

enum class ChatChannel(val displayName: String) {
    GLOBAL("Global"),
    NEGOTIATIONS("Negociações P2P"),
    CORPORATIONS("Corporações")
}

data class ChatMessage(
    val messageId: String,
    val senderName: String,
    val senderCorp: String,
    val channel: ChatChannel,
    val text: String,
    val tradeOffer: TradeOfferCard? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class VehicleType(
    val title: String,
    val capacityTons: Double,
    val speedKmh: Double,
    val baseFreightRevenuePerSec: Double,
    val fuelCostPerSec: Double,
    val unlockEra: GameEra
) {
    TRUCK("Frota de Caminhões Scania 6x4", 30.0, 80.0, 25.0, 4.0, GameEra.LOCAL_AGROINDUSTRY),
    FREIGHT_TRAIN("Composição Ferroviária GE C30", 250.0, 60.0, 110.0, 12.0, GameEra.URBAN_INDUSTRIAL_HUB),
    CONTAINER_SHIP("Porta-Contêiner Panamax", 2500.0, 35.0, 650.0, 35.0, GameEra.URBAN_INDUSTRIAL_HUB),
    CARGO_JET("Jato Cargueiro Boeing 777F", 100.0, 850.0, 1400.0, 95.0, GameEra.TECH_LOGISTICS_CONGLOMERATE),
    ORBITAL_ROCKET("Veículo de Carga Orbital Starship", 150.0, 27000.0, 9500.0, 450.0, GameEra.SPACE_CORPORATION)
}

data class FleetRoute(
    val routeId: String,
    val name: String,
    val vehicleType: VehicleType,
    val count: Int,
    val origin: String,
    val destination: String,
    val activeCargoProductId: String? = null,
    val isActive: Boolean = true,
    val tripProgress: Float = 0f
)
