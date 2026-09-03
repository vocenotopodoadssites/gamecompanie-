package com.example.ai

import com.example.BuildConfig
import com.example.data.model.CustomMachine
import com.example.data.model.PhysicalState
import com.example.data.model.Product
import com.example.data.model.ProductCatalog
import com.example.data.model.ProductCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.roundToInt

class GeminiInventionService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateInvention(
        selectedComponents: List<Product>,
        userPrompt: String
    ): Pair<CustomMachine, Product> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasValidKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (hasValidKey) {
            try {
                val aiResult = callGeminiApi(apiKey, selectedComponents, userPrompt)
                if (aiResult != null) {
                    return@withContext aiResult
                }
            } catch (e: Exception) {
                // Fallback to procedural synthesis if API call fails
                e.printStackTrace()
            }
        }

        // Procedural generator
        synthesizeProceduralInvention(selectedComponents, userPrompt)
    }

    private fun callGeminiApi(
        apiKey: String,
        components: List<Product>,
        userPrompt: String
    ): Pair<CustomMachine, Product>? {
        val componentListStr = components.joinToString(", ") { "${it.name} (${it.category.displayName}, ${it.physicalState.label})" }
        val promptText = """
            Você é um motor de IA de engenharia industrial para um jogo Tycoon espacial.
            O jogador combinou os seguintes componentes na bancada de P&D:
            [$componentListStr]
            Diretriz ou nome sugerido pelo jogador: "${if (userPrompt.isBlank()) "Criação de alta performance" else userPrompt}"
            
            Gere uma especificação em JSON puro (sem markdown) com as seguintes chaves:
            {
              "name": "Nome técnico e comercial impactante do novo maquinário ou produto híbrido",
              "category": "Maquinário Híbrido" ou "Produto Especial",
              "powerConsumptionKw": número float entre 5.0 e 150.0,
              "productionMultiplier": número float entre 1.2 e 5.0,
              "durabilityHrs": número inteiro entre 100 e 2000,
              "weightKg": número float entre 20.0 e 8000.0,
              "estimatedValue": número float entre 500.0 e 50000.0,
              "physicalState": "SOLID", "LIQUID", "GAS" ou "PLASMA",
              "iconShape": "GEAR_HYBRID", "CONVEYOR_ARM", "PLASMA_REACTOR", "ORBITAL_POD" ou "BIO_VAT",
              "technicalSummary": "Descrição técnica concisa de 1 a 2 frases explicando como os insumos interagem física e quimicamente."
            }
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", promptText) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("responseMimeType", "application/json")
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val responseBodyStr = response.body?.string() ?: return null
            val responseJson = JSONObject(responseBodyStr)
            val candidates = responseJson.optJSONArray("candidates") ?: return null
            val firstCandidate = candidates.optJSONObject(0) ?: return null
            val contentObj = firstCandidate.optJSONObject("content") ?: return null
            val parts = contentObj.optJSONArray("parts") ?: return null
            val text = parts.optJSONObject(0)?.optString("text") ?: return null

            val parsed = JSONObject(text.trim())
            val name = parsed.optString("name", "Invenção Híbrida IA")
            val category = parsed.optString("category", "Maquinário Híbrido")
            val powerKw = parsed.optDouble("powerConsumptionKw", 25.0)
            val mult = parsed.optDouble("productionMultiplier", 1.8)
            val durability = parsed.optInt("durabilityHrs", 500)
            val weight = parsed.optDouble("weightKg", 450.0)
            val estVal = parsed.optDouble("estimatedValue", 1200.0)
            val stateStr = parsed.optString("physicalState", "SOLID")
            val state = try { PhysicalState.valueOf(stateStr) } catch (e: Exception) { PhysicalState.SOLID }
            val iconShape = parsed.optString("iconShape", "GEAR_HYBRID")
            val summary = parsed.optString("technicalSummary", "Módulo otimizado por rede neural autônoma.")

            val machineId = "custom_" + UUID.randomUUID().toString().take(8)
            val prodId = "prod_" + machineId

            val machine = CustomMachine(
                id = machineId,
                name = name,
                category = category,
                promptUsed = userPrompt,
                componentNames = components.map { it.name },
                powerConsumptionKw = (powerKw * 10).roundToInt() / 10.0,
                productionMultiplier = (mult * 10).roundToInt() / 10.0,
                durabilityHrs = durability,
                weightKg = (weight * 10).roundToInt() / 10.0,
                estimatedValue = estVal,
                physicalState = state,
                outputProductId = prodId,
                primaryColorHex = pickColorForCategory(category, iconShape),
                secondaryColorHex = 0xFF10B981,
                iconShape = iconShape,
                technicalSummary = summary
            )

            val product = Product(
                id = prodId,
                name = name,
                category = ProductCategory.AI_CUSTOM,
                physicalState = state,
                densityKgM3 = (weight / 0.5).coerceIn(50.0, 15000.0),
                reactivity = if (powerKw > 60) "Alta (Exotérmica)" else "Estabilizada",
                basePrice = estVal,
                currentMarketPrice = estVal,
                description = summary,
                isCustomAi = true,
                powerConsumptionKw = powerKw,
                durabilityHrs = durability,
                productionRatePerMin = mult * 2.0,
                iconKey = "ai_tech"
            )

            return Pair(machine, product)
        }
    }

    fun synthesizeProceduralInvention(
        components: List<Product>,
        userPrompt: String
    ): Pair<CustomMachine, Product> {
        val totalBaseVal = components.sumOf { it.basePrice }
        val avgDensity = if (components.isNotEmpty()) components.map { it.densityKgM3 }.average() else 1000.0
        val isEnergyRich = components.any { it.category == ProductCategory.ENERGY_BASIC || it.category == ProductCategory.ADVANCED_CHEMICAL }
        val isMechanical = components.any { it.category == ProductCategory.MECHANICAL_PARTS }
        val isHighTech = components.any { it.category == ProductCategory.HIGH_TECH_SPACE }

        val name = when {
            userPrompt.isNotBlank() -> userPrompt.trim()
            isHighTech && isMechanical -> "Módulo Sinergético Cósmico v${(1..9).random()}"
            isMechanical && components.size >= 3 -> "Unidade Multitarefa Autômata Mk-${(10..99).random()}"
            isEnergyRich && isMechanical -> "Conversor Eletromecânico Dinâmico"
            components.size == 2 -> "Híbrido ${components[0].name.split(" ").first()} & ${components[1].name.split(" ").first()}"
            else -> "Sistema Customizado Nova Geração"
        }

        val category = if (isMechanical || components.any { it.name.contains("Motor") || it.name.contains("Chassi") }) {
            "Maquinário Híbrido"
        } else {
            "Produto Especial"
        }

        val basePower = when {
            isHighTech -> 85.0 + (components.size * 12.0)
            isMechanical -> 28.0 + (components.size * 6.5)
            else -> 15.0
        }

        val multiplier = 1.35 + (components.size * 0.42) + if (isHighTech) 1.2 else 0.0
        val durability = 350 + (components.size * 180) + (if (isMechanical) 300 else 0)
        val weight = max(25.0, (avgDensity * 0.15 * components.size))
        val value = max(250.0, totalBaseVal * (1.8 + (components.size * 0.5)))

        val shape = when {
            isHighTech -> "ORBITAL_POD"
            isEnergyRich -> "PLASMA_REACTOR"
            isMechanical -> "CONVEYOR_ARM"
            else -> "GEAR_HYBRID"
        }

        val machineId = "custom_" + UUID.randomUUID().toString().take(8)
        val prodId = "prod_" + machineId

        val summary = buildString {
            append("Arranjo físico de alta sinergia unindo ")
            append(components.joinToString(" + ") { it.name })
            append(". ")
            if (isMechanical) append("Acoplamento de eixos dinâmico com balanceamento harmônico. ")
            if (isEnergyRich) append("Circuito de regeneração térmica reduz perda de fluxo. ")
            if (isHighTech) append("Integrado com telemetria quântica de malha fechada.")
        }

        val machine = CustomMachine(
            id = machineId,
            name = name,
            category = category,
            promptUsed = userPrompt,
            componentNames = components.map { it.name },
            powerConsumptionKw = (basePower * 10).roundToInt() / 10.0,
            productionMultiplier = (multiplier * 10).roundToInt() / 10.0,
            durabilityHrs = durability,
            weightKg = (weight * 10).roundToInt() / 10.0,
            estimatedValue = (value * 10).roundToInt() / 10.0,
            physicalState = if (isEnergyRich && components.any { it.physicalState == PhysicalState.LIQUID }) PhysicalState.LIQUID else PhysicalState.SOLID,
            outputProductId = prodId,
            primaryColorHex = pickColorForCategory(category, shape),
            secondaryColorHex = 0xFFF59E0B,
            iconShape = shape,
            technicalSummary = summary
        )

        val product = Product(
            id = prodId,
            name = name,
            category = ProductCategory.AI_CUSTOM,
            physicalState = machine.physicalState,
            densityKgM3 = avgDensity,
            reactivity = if (basePower > 50) "Alta" else "Controlada",
            basePrice = value,
            currentMarketPrice = value,
            description = summary,
            isCustomAi = true,
            powerConsumptionKw = machine.powerConsumptionKw,
            durabilityHrs = durability,
            productionRatePerMin = multiplier * 2.5,
            iconKey = "ai_tech"
        )

        return Pair(machine, product)
    }

    private fun pickColorForCategory(category: String, shape: String): Long {
        return when (shape) {
            "ORBITAL_POD" -> 0xFF8B5CF6 // purple
            "PLASMA_REACTOR" -> 0xFFEF4444 // red
            "CONVEYOR_ARM" -> 0xFF3B82F6 // blue
            else -> 0xFF0D9488 // teal
        }
    }
}
