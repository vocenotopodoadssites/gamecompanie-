package com.example.data.model

enum class SectorType(val label: String) {
    PRIMARY("Setor Primário (Extração & Energia)"),
    SECONDARY("Setor Secundário (Transformação & Química)"),
    CAPITAL_GOODS("Bens de Capital (Maquinário & Engenharia)"),
    TERTIARY("Setor Terciário (Logística & Serviços)"),
    QUATERNARY_SPACE("Quaternário & Espacial")
}

data class BusinessRecipe(
    val inputItemIds: List<Pair<String, Int>> = emptyList(),
    val outputItemId: String,
    val outputQuantity: Int = 1,
    val cycleTimeSeconds: Int = 3,
    val energyCostKw: Double = 5.0
)

data class BusinessType(
    val id: String,
    val number: Int,
    val name: String,
    val sector: SectorType,
    val requiredEra: GameEra,
    val baseCost: Double,
    val description: String,
    val recipe: BusinessRecipe,
    val revenuePerSecond: Double = 0.0,
    val logisticsCapacityTon: Double = 0.0,
    val iconKey: String
)

object BusinessCatalog {
    val ALL_50_BUSINESSES: List<BusinessType> = listOf(
        // --- 1 to 15: Setor Primário ---
        BusinessType(
            id = "biz_01", number = 1, name = "Fazenda Agrícola Geral",
            sector = SectorType.PRIMARY, requiredEra = GameEra.SUBSISTENCE_FARM,
            baseCost = 1000.0, description = "Cultivo de milho, soja e trigo com tração mecânica.",
            recipe = BusinessRecipe(outputItemId = "p_01", outputQuantity = 2, cycleTimeSeconds = 3, energyCostKw = 1.0),
            iconKey = "grain"
        ),
        BusinessType(
            id = "biz_02", number = 2, name = "Pomar e Viticultura",
            sector = SectorType.PRIMARY, requiredEra = GameEra.SUBSISTENCE_FARM,
            baseCost = 1500.0, description = "Colheita de maçãs silvestres e laranjas Valencia selecionadas.",
            recipe = BusinessRecipe(outputItemId = "p_03", outputQuantity = 2, cycleTimeSeconds = 3, energyCostKw = 1.0),
            iconKey = "apple"
        ),
        BusinessType(
            id = "biz_03", number = 3, name = "Pecuária de Corte e Leite",
            sector = SectorType.PRIMARY, requiredEra = GameEra.SUBSISTENCE_FARM,
            baseCost = 2800.0, description = "Manejo bovino sustentável gerando carne in natura e leite cru.",
            recipe = BusinessRecipe(outputItemId = "p_08", outputQuantity = 3, cycleTimeSeconds = 4, energyCostKw = 2.0),
            iconKey = "cow"
        ),
        BusinessType(
            id = "biz_04", number = 4, name = "Avicultura e Apicultura",
            sector = SectorType.PRIMARY, requiredEra = GameEra.SUBSISTENCE_FARM,
            baseCost = 2200.0, description = "Criação de galinhas poedeiras e colmeias de mel silvestre.",
            recipe = BusinessRecipe(outputItemId = "p_09", outputQuantity = 4, cycleTimeSeconds = 3, energyCostKw = 1.5),
            iconKey = "egg"
        ),
        BusinessType(
            id = "biz_05", number = 5, name = "Plantação Hidropônica Avançada",
            sector = SectorType.PRIMARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 12000.0, description = "Nutrição mineral direta sem solo com economia de 95% de água.",
            recipe = BusinessRecipe(outputItemId = "p_02", outputQuantity = 6, cycleTimeSeconds = 3, energyCostKw = 4.0),
            iconKey = "water_drop"
        ),
        BusinessType(
            id = "biz_06", number = 6, name = "Mina de Minério de Ferro e Carvão",
            sector = SectorType.PRIMARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 18000.0, description = "Lavra a céu aberto de blocos brutos de ferro e carvão mineral.",
            recipe = BusinessRecipe(outputItemId = "p_11", outputQuantity = 4, cycleTimeSeconds = 4, energyCostKw = 8.0),
            iconKey = "mountain"
        ),
        BusinessType(
            id = "biz_07", number = 7, name = "Extração de Metais Preciosos (Ouro/Prata)",
            sector = SectorType.PRIMARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 35000.0, description = "Garimpo e lixiviação gravimétrica de veios de ouro e prata nativos.",
            recipe = BusinessRecipe(outputItemId = "p_52", outputQuantity = 1, cycleTimeSeconds = 8, energyCostKw = 12.0),
            iconKey = "gold"
        ),
        BusinessType(
            id = "biz_08", number = 8, name = "Jazida de Terras Raras e Lítio",
            sector = SectorType.PRIMARY, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 85000.0, description = "Extração de salmouras de espodumênio ricas em lítio e neodímio.",
            recipe = BusinessRecipe(outputItemId = "p_54", outputQuantity = 2, cycleTimeSeconds = 5, energyCostKw = 16.0),
            iconKey = "battery"
        ),
        BusinessType(
            id = "biz_09", number = 9, name = "Pedreira e Extração de Calcário/Silica",
            sector = SectorType.PRIMARY, requiredEra = GameEra.SUBSISTENCE_FARM,
            baseCost = 4500.0, description = "Desmonte de rochas sedimentares com britagem primária.",
            recipe = BusinessRecipe(outputItemId = "p_18", outputQuantity = 5, cycleTimeSeconds = 3, energyCostKw = 5.0),
            iconKey = "rocks"
        ),
        BusinessType(
            id = "biz_10", number = 10, name = "Poço de Petróleo e Gás Natural",
            sector = SectorType.PRIMARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 42000.0, description = "Coluna de perfuração profunda com extração de óleo cru e gás.",
            recipe = BusinessRecipe(outputItemId = "p_14", outputQuantity = 3, cycleTimeSeconds = 4, energyCostKw = 10.0),
            iconKey = "oil_rig"
        ),
        BusinessType(
            id = "biz_11", number = 11, name = "Usina Solar Fotovoltaica",
            sector = SectorType.PRIMARY, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 65000.0, description = "Parque solar com inversores centralizados injetando energia limpa.",
            recipe = BusinessRecipe(outputItemId = "p_16", outputQuantity = 6, cycleTimeSeconds = 3, energyCostKw = 0.0),
            iconKey = "solar"
        ),
        BusinessType(
            id = "biz_12", number = 12, name = "Parque Eólico",
            sector = SectorType.PRIMARY, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 75000.0, description = "Aerogeradores com pás compósitas de alta velocidade média de vento.",
            recipe = BusinessRecipe(outputItemId = "p_16", outputQuantity = 7, cycleTimeSeconds = 3, energyCostKw = 0.0),
            iconKey = "wind"
        ),
        BusinessType(
            id = "biz_13", number = 13, name = "Usina Hidrelétrica de Médio Porte",
            sector = SectorType.PRIMARY, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 140000.0, description = "Turbinas Francis acopladas gerando alta densidade de MWh contínuos.",
            recipe = BusinessRecipe(outputItemId = "p_16", outputQuantity = 12, cycleTimeSeconds = 2, energyCostKw = 0.0),
            iconKey = "hydro"
        ),
        BusinessType(
            id = "biz_14", number = 14, name = "Usina Termelétrica a Biomassa",
            sector = SectorType.PRIMARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 28000.0, description = "Queima controlada de bagaço de cana e biogás para vapor de alta pressão.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_05" to 2), outputItemId = "p_16", outputQuantity = 4, cycleTimeSeconds = 3, energyCostKw = 0.0),
            iconKey = "thermal"
        ),
        BusinessType(
            id = "biz_15", number = 15, name = "Fazenda de Captura e Processamento de Água",
            sector = SectorType.PRIMARY, requiredEra = GameEra.SUBSISTENCE_FARM,
            baseCost = 3200.0, description = "Poços artesianos com osmose reversa e leito filtrante de areia.",
            recipe = BusinessRecipe(outputItemId = "p_15", outputQuantity = 8, cycleTimeSeconds = 2, energyCostKw = 2.0),
            iconKey = "water_well"
        ),

        // --- 16 to 26: Setor Secundário ---
        BusinessType(
            id = "biz_16", number = 16, name = "Moinhos e Silos de Grãos",
            sector = SectorType.SECONDARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 15000.0, description = "Moagem de trigo e soja em farinha e farelo de alta pureza.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_06" to 2), outputItemId = "p_22", outputQuantity = 2, cycleTimeSeconds = 3, energyCostKw = 4.0),
            iconKey = "mill"
        ),
        BusinessType(
            id = "biz_17", number = 17, name = "Destilaria e Fermentaria",
            sector = SectorType.SECONDARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 22000.0, description = "Fermentação de mosto e colunas fracionadas de álcool etílico.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_05" to 3), outputItemId = "p_24", outputQuantity = 2, cycleTimeSeconds = 4, energyCostKw = 6.0),
            iconKey = "distillery"
        ),
        BusinessType(
            id = "biz_18", number = 18, name = "Indústria de Laticínios e Frios",
            sector = SectorType.SECONDARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 26000.0, description = "Pasteurização e processamento térmico de laticínios refrigerados.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_08" to 4), outputItemId = "p_49", outputQuantity = 1, cycleTimeSeconds = 5, energyCostKw = 5.0),
            iconKey = "cheese"
        ),
        BusinessType(
            id = "biz_19", number = 19, name = "Refinaria de Petróleo e Petroquímica",
            sector = SectorType.SECONDARY, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 190000.0, description = "Craqueamento catalítico de cru gerando gasolina, diesel e GLP.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_14" to 3), outputItemId = "p_26", outputQuantity = 3, cycleTimeSeconds = 4, energyCostKw = 15.0),
            iconKey = "refinery"
        ),
        BusinessType(
            id = "biz_20", number = 20, name = "Fundição de Metais Básicos",
            sector = SectorType.SECONDARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 32000.0, description = "Alto-forno com conversão de minério e carvão em lingotes de ferro.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_11" to 2, "p_12" to 1), outputItemId = "p_28", outputQuantity = 2, cycleTimeSeconds = 4, energyCostKw = 12.0),
            iconKey = "foundry"
        ),
        BusinessType(
            id = "biz_21", number = 21, name = "Siderurgia e Metalurgia de Alta Precisão",
            sector = SectorType.SECONDARY, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 210000.0, description = "Laminação a quente de chapas estruturais de aço carbono.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_28" to 2), outputItemId = "p_29", outputQuantity = 2, cycleTimeSeconds = 3, energyCostKw = 18.0),
            iconKey = "steel"
        ),
        BusinessType(
            id = "biz_22", number = 22, name = "Indústria de Polímeros e Borracha Sintética",
            sector = SectorType.SECONDARY, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 130000.0, description = "Polimerização de nafta para plásticos industriais e elastômeros.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_14" to 2), outputItemId = "p_57", outputQuantity = 3, cycleTimeSeconds = 3, energyCostKw = 10.0),
            iconKey = "plastic"
        ),
        BusinessType(
            id = "biz_23", number = 23, name = "Fábrica de Fertilizantes e Defensivos",
            sector = SectorType.SECONDARY, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 110000.0, description = "Síntese Haber-Bosch e granulação de fertilizantes NPK concentrados.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_18" to 2), outputItemId = "p_59", outputQuantity = 3, cycleTimeSeconds = 4, energyCostKw = 9.0),
            iconKey = "fertilizer"
        ),
        BusinessType(
            id = "biz_24", number = 24, name = "Laboratório de Síntese Química Fina",
            sector = SectorType.SECONDARY, requiredEra = GameEra.TECH_LOGISTICS_CONGLOMERATE,
            baseCost = 480000.0, description = "Reatores de fluxo contínuo para ácidos de pureza eletrônica e catalisadores.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_60" to 1), outputItemId = "p_62", outputQuantity = 1, cycleTimeSeconds = 6, energyCostKw = 20.0),
            iconKey = "flask"
        ),
        BusinessType(
            id = "biz_25", number = 25, name = "Indústria de Celulose e Embalagens",
            sector = SectorType.SECONDARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 45000.0, description = "Digestão química de cavacos para caixas, sacos e bobinas.",
            recipe = BusinessRecipe(outputItemId = "p_19", outputQuantity = 4, cycleTimeSeconds = 3, energyCostKw = 7.0),
            iconKey = "package"
        ),
        BusinessType(
            id = "biz_26", number = 26, name = "Fábrica de Vidros e Cerâmicas Técnicas",
            sector = SectorType.SECONDARY, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 95000.0, description = "Forno float térmico de alta temperatura para vidros planos e isoladores.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_13" to 3), outputItemId = "p_30", outputQuantity = 3, cycleTimeSeconds = 3, energyCostKw = 14.0),
            iconKey = "glass"
        ),

        // --- 27 to 35: Setor de Bens de Capital ---
        BusinessType(
            id = "biz_27", number = 27, name = "Oficina de Engenharia Reversa e Prototipagem",
            sector = SectorType.CAPITAL_GOODS, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 38000.0, description = "Tornos CNC manuais e conformação de molas e parafusos de aço.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_28" to 1), outputItemId = "p_31", outputQuantity = 4, cycleTimeSeconds = 3, energyCostKw = 6.0),
            iconKey = "wrench"
        ),
        BusinessType(
            id = "biz_28", number = 28, name = "Montadora de Maquinário Agrícola Customizado",
            sector = SectorType.CAPITAL_GOODS, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 280000.0, description = "Integração de chassis reforçados com esteiras e motores térmicos.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_36" to 1, "p_37" to 1, "p_44" to 1), outputItemId = "p_45", outputQuantity = 2, cycleTimeSeconds = 5, energyCostKw = 16.0),
            iconKey = "tractor"
        ),
        BusinessType(
            id = "biz_29", number = 29, name = "Fábrica de Esteiras e Sistemas de Transporte",
            sector = SectorType.CAPITAL_GOODS, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 160000.0, description = "Fabricação de roletes, correias síncronas e esteiras modulares industriais.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_34" to 2, "p_31" to 2), outputItemId = "p_45", outputQuantity = 2, cycleTimeSeconds = 3, energyCostKw = 11.0),
            iconKey = "conveyor"
        ),
        BusinessType(
            id = "biz_30", number = 30, name = "Indústria de Robótica e Braços Mecânicos",
            sector = SectorType.CAPITAL_GOODS, requiredEra = GameEra.TECH_LOGISTICS_CONGLOMERATE,
            baseCost = 650000.0, description = "Braços de 6 eixos com servomotores digitais e microcontroladores.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_74" to 2, "p_39" to 1), outputItemId = "p_46", outputQuantity = 1, cycleTimeSeconds = 6, energyCostKw = 22.0),
            iconKey = "robot"
        ),
        BusinessType(
            id = "biz_31", number = 31, name = "Fábrica de Geradores e Motores de Alta Potência",
            sector = SectorType.CAPITAL_GOODS, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 240000.0, description = "Bobinamento automatizado com fiação isolada e estatores de indução.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_40" to 2, "p_32" to 2), outputItemId = "p_38", outputQuantity = 1, cycleTimeSeconds = 5, energyCostKw = 15.0),
            iconKey = "motor"
        ),
        BusinessType(
            id = "biz_32", number = 32, name = "Construtora de Módulos Industriais Modulares",
            sector = SectorType.CAPITAL_GOODS, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 310000.0, description = "Soldagem robótica pesada de chassis modulares para fábricas rápidas.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_29" to 2), outputItemId = "p_36", outputQuantity = 2, cycleTimeSeconds = 4, energyCostKw = 14.0),
            iconKey = "frame"
        ),
        BusinessType(
            id = "biz_33", number = 33, name = "Estúdio de Design e Engenharia de Materiais",
            sector = SectorType.CAPITAL_GOODS, requiredEra = GameEra.TECH_LOGISTICS_CONGLOMERATE,
            baseCost = 520000.0, description = "Desenvolvimento de compósitos ultraleves de fibra de carbono.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_63" to 2), outputItemId = "p_64", outputQuantity = 2, cycleTimeSeconds = 5, energyCostKw = 18.0),
            iconKey = "design"
        ),
        BusinessType(
            id = "biz_34", number = 34, name = "Laboratório de Eletrônica de Potência",
            sector = SectorType.CAPITAL_GOODS, requiredEra = GameEra.TECH_LOGISTICS_CONGLOMERATE,
            baseCost = 590000.0, description = "Montagem de PCBs SMD, inversores IGBT e painéis analógicos/digitais.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_51" to 1, "p_30" to 1), outputItemId = "p_39", outputQuantity = 3, cycleTimeSeconds = 4, energyCostKw = 15.0),
            iconKey = "pcb"
        ),
        BusinessType(
            id = "biz_35", number = 35, name = "Oficina de Blindagem e Chassi Reforçado",
            sector = SectorType.CAPITAL_GOODS, requiredEra = GameEra.TECH_LOGISTICS_CONGLOMERATE,
            baseCost = 780000.0, description = "Fundição sob vácuo de titânio para fuselagens de alta pressão.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_48" to 2, "p_29" to 2), outputItemId = "p_76", outputQuantity = 1, cycleTimeSeconds = 6, energyCostKw = 25.0),
            iconKey = "shield"
        ),

        // --- 36 to 44: Setor Terciário Avançado e Serviços ---
        BusinessType(
            id = "biz_36", number = 36, name = "Companhia Logística e Frota de Caminhões",
            sector = SectorType.TERTIARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 50000.0, description = "Caminhões pesados transportando insumos e gerando receita de frete.",
            recipe = BusinessRecipe(outputItemId = "p_26", outputQuantity = 0, cycleTimeSeconds = 1),
            revenuePerSecond = 85.0, logisticsCapacityTon = 50.0, iconKey = "truck"
        ),
        BusinessType(
            id = "biz_37", number = 37, name = "Companhia Ferroviária de Carga",
            sector = SectorType.TERTIARY, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 380000.0, description = "Composições ferroviárias diesel-elétricas para granéis em larga escala.",
            recipe = BusinessRecipe(outputItemId = "p_28", outputQuantity = 0, cycleTimeSeconds = 1),
            revenuePerSecond = 340.0, logisticsCapacityTon = 350.0, iconKey = "train"
        ),
        BusinessType(
            id = "biz_38", number = 38, name = "Companhia de Navegação e Portos Fluviais/Marítimos",
            sector = SectorType.TERTIARY, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 900000.0, description = "Navios porta-contêineres e berços alfandegados intercontinentais.",
            recipe = BusinessRecipe(outputItemId = "p_14", outputQuantity = 0, cycleTimeSeconds = 1),
            revenuePerSecond = 820.0, logisticsCapacityTon = 2000.0, iconKey = "ship"
        ),
        BusinessType(
            id = "biz_39", number = 39, name = "Fretamento de Transporte Aéreo de Carga",
            sector = SectorType.TERTIARY, requiredEra = GameEra.TECH_LOGISTICS_CONGLOMERATE,
            baseCost = 1800000.0, description = "Jatos cargueiros de longo curso para componentes eletrônicos urgentes.",
            recipe = BusinessRecipe(outputItemId = "p_39", outputQuantity = 0, cycleTimeSeconds = 1),
            revenuePerSecond = 1750.0, logisticsCapacityTon = 120.0, iconKey = "airplane"
        ),
        BusinessType(
            id = "biz_40", number = 40, name = "Centros de Distribuição e Armazéns Frios",
            sector = SectorType.TERTIARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 95000.0, description = "Hub climatizado com cross-docking reduzindo perdas e custos de inventário.",
            recipe = BusinessRecipe(outputItemId = "p_49", outputQuantity = 0, cycleTimeSeconds = 1),
            revenuePerSecond = 120.0, logisticsCapacityTon = 500.0, iconKey = "warehouse"
        ),
        BusinessType(
            id = "biz_41", number = 41, name = "Cooperativa de Crédito e Financiamento",
            sector = SectorType.TERTIARY, requiredEra = GameEra.LOCAL_AGROINDUSTRY,
            baseCost = 150000.0, description = "Geração passiva de juros e linhas de adiantamento de recebíveis.",
            recipe = BusinessRecipe(outputItemId = "p_16", outputQuantity = 0, cycleTimeSeconds = 1),
            revenuePerSecond = 260.0, iconKey = "bank"
        ),
        BusinessType(
            id = "biz_42", number = 42, name = "Bolsa de Commodities e Derivativos Internos",
            sector = SectorType.TERTIARY, requiredEra = GameEra.TECH_LOGISTICS_CONGLOMERATE,
            baseCost = 2500000.0, description = "Taxa de liquidação automática sobre todas as transações de mercado.",
            recipe = BusinessRecipe(outputItemId = "p_52", outputQuantity = 0, cycleTimeSeconds = 1),
            revenuePerSecond = 2900.0, iconKey = "chart"
        ),
        BusinessType(
            id = "biz_43", number = 43, name = "Empresa de Consultoria em Eficiência Energética",
            sector = SectorType.TERTIARY, requiredEra = GameEra.URBAN_INDUSTRIAL_HUB,
            baseCost = 220000.0, description = "Auditorias térmicas industriais e gestão de demanda em tempo real.",
            recipe = BusinessRecipe(outputItemId = "p_16", outputQuantity = 0, cycleTimeSeconds = 1),
            revenuePerSecond = 310.0, iconKey = "lightbulb"
        ),
        BusinessType(
            id = "biz_44", number = 44, name = "Central de Dados (Data Center) e Computação Industrial",
            sector = SectorType.TERTIARY, requiredEra = GameEra.TECH_LOGISTICS_CONGLOMERATE,
            baseCost = 3800000.0, description = "Servidores em rack refrigerados por imersão para inteligência fabril.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_16" to 4), outputItemId = "p_72", outputQuantity = 1, cycleTimeSeconds = 5, energyCostKw = 35.0),
            revenuePerSecond = 4200.0, iconKey = "server"
        ),

        // --- 45 to 50: Setor Quaternário e Espacial ---
        BusinessType(
            id = "biz_45", number = 45, name = "Centro de Pesquisa e Desenvolvimento (P&D) Aeroespacial",
            sector = SectorType.QUATERNARY_SPACE, requiredEra = GameEra.TECH_LOGISTICS_CONGLOMERATE,
            baseCost = 7500000.0, description = "Túneis de vento supersônicos e simulação de dinâmica de plasma estelar.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_72" to 1, "p_64" to 1), outputItemId = "p_81", outputQuantity = 1, cycleTimeSeconds = 6, energyCostKw = 45.0),
            revenuePerSecond = 6500.0, iconKey = "telescope"
        ),
        BusinessType(
            id = "biz_46", number = 46, name = "Estaleiro de Foguetes e Veículos de Lançamento",
            sector = SectorType.QUATERNARY_SPACE, requiredEra = GameEra.SPACE_CORPORATION,
            baseCost = 22000000.0, description = "Montagem vertical de veículos pesados reutilizáveis para órbita.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_76" to 2, "p_81" to 1, "p_67" to 3), outputItemId = "p_100", outputQuantity = 1, cycleTimeSeconds = 10, energyCostKw = 80.0),
            revenuePerSecond = 18000.0, logisticsCapacityTon = 5000.0, iconKey = "rocket"
        ),
        BusinessType(
            id = "biz_47", number = 47, name = "Fábrica de Propulsores Iônicos e Combustível Espacial",
            sector = SectorType.QUATERNARY_SPACE, requiredEra = GameEra.SPACE_CORPORATION,
            baseCost = 28000000.0, description = "Liquefação criogênica de LOX e usinagem de propulsores de efeito Hall.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_15" to 4), outputItemId = "p_67", outputQuantity = 4, cycleTimeSeconds = 4, energyCostKw = 50.0),
            revenuePerSecond = 24000.0, iconKey = "flame"
        ),
        BusinessType(
            id = "biz_48", number = 48, name = "Mineração Orbital de Asteroides",
            sector = SectorType.QUATERNARY_SPACE, requiredEra = GameEra.SPACE_CORPORATION,
            baseCost = 65000000.0, description = "Captura de asteroides próximos da Terra para colheita massiva de platina.",
            recipe = BusinessRecipe(outputItemId = "p_85", outputQuantity = 2, cycleTimeSeconds = 8, energyCostKw = 90.0),
            revenuePerSecond = 55000.0, iconKey = "asteroid"
        ),
        BusinessType(
            id = "biz_49", number = 49, name = "Central de Telecomunicações Via Satélite",
            sector = SectorType.QUATERNARY_SPACE, requiredEra = GameEra.SPACE_CORPORATION,
            baseCost = 45000000.0, description = "Roteamento de banda ultralarga e chaves de segurança quântica orbital.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_82" to 1), outputItemId = "p_99", outputQuantity = 2, cycleTimeSeconds = 6, energyCostKw = 60.0),
            revenuePerSecond = 38000.0, iconKey = "satellite"
        ),
        BusinessType(
            id = "biz_50", number = 50, name = "Administração de Colônia Espacial e Habitat",
            sector = SectorType.QUATERNARY_SPACE, requiredEra = GameEra.SPACE_CORPORATION,
            baseCost = 150000000.0, description = "Governança de domos ecológicos autônomos, reatores de fusão e hélio-3.",
            recipe = BusinessRecipe(inputItemIds = listOf("p_84" to 1, "p_88" to 1), outputItemId = "p_89", outputQuantity = 2, cycleTimeSeconds = 12, energyCostKw = 150.0),
            revenuePerSecond = 120000.0, logisticsCapacityTon = 25000.0, iconKey = "colony"
        )
    )

    fun findById(id: String): BusinessType? = ALL_50_BUSINESSES.find { it.id == id }
}
