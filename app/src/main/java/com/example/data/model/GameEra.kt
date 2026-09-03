package com.example.data.model

enum class GameEra(
    val stage: Int,
    val title: String,
    val subtitle: String,
    val requiredNetWorth: Double,
    val description: String
) {
    SUBSISTENCE_FARM(
        stage = 1,
        title = "Fazenda de Subsistência",
        subtitle = "Cultivo manual, extração rudimentar e biomassa",
        requiredNetWorth = 0.0,
        description = "Início obrigatório: agricultura familiar, coleta de sementes, minerais superficiais e tração animal."
    ),
    LOCAL_AGROINDUSTRY(
        stage = 2,
        title = "Agroindústria Local",
        subtitle = "Processamento primário, prensagem e logística municipal",
        requiredNetWorth = 25000.0,
        description = "Transformação de colheitas em insumos intermediários, moagem, destilação e frotas de entrega locais."
    ),
    URBAN_INDUSTRIAL_HUB(
        stage = 3,
        title = "Polo Industrial Urbano",
        subtitle = "Linhas automatizadas, metalurgia fina e rede elétrica",
        requiredNetWorth = 250000.0,
        description = "Chaminés a todo vapor: siderúrgicas pesadas, química fina, usinas elétricas e padronização mecânica."
    ),
    TECH_LOGISTICS_CONGLOMERATE(
        stage = 4,
        title = "Conglomerado Tecnológico e Logístico",
        subtitle = "Robótica, semicondutores, data centers e mercado de capitais",
        requiredNetWorth = 2500000.0,
        description = "Cadeias globais de suprimento intermodal, microchips de alta densidade, automação por robôs e finanças avançadas."
    ),
    SPACE_CORPORATION(
        stage = 5,
        title = "Corporação Espacial",
        subtitle = "Mineração de asteroides, propulsão iônica e colônias orbitais",
        requiredNetWorth = 25000000.0,
        description = "Domínio interplanetário: estaleiros orbitais de foguetes, extração de hélio-3 e habitats extraterrestres auto-sustentáveis."
    );

    companion object {
        fun fromStage(stage: Int): GameEra = entries.find { it.stage == stage } ?: SUBSISTENCE_FARM
    }
}
