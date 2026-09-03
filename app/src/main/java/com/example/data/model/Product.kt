package com.example.data.model

enum class ProductCategory(val displayName: String) {
    RAW_NATURAL("Matéria-Prima Natural"),
    ENERGY_BASIC("Insumos Básicos & Energia"),
    MECHANICAL_PARTS("Componentes Mecânicos"),
    ADVANCED_CHEMICAL("Química Fina & Materiais"),
    HIGH_TECH_SPACE("Alta Tecnologia & Aeroespacial"),
    AI_CUSTOM("Criação IA Customizada")
}

enum class PhysicalState(val label: String) {
    SOLID("Sólido"),
    LIQUID("Líquido"),
    GAS("Gasoso"),
    PLASMA("Plasma"),
    DIGITAL("Digital")
}

data class Product(
    val id: String,
    val name: String,
    val category: ProductCategory,
    val physicalState: PhysicalState,
    val densityKgM3: Double,
    val reactivity: String,
    val basePrice: Double,
    val currentMarketPrice: Double = basePrice,
    val priceChangePct: Double = 0.0,
    val description: String,
    val isCustomAi: Boolean = false,
    val powerConsumptionKw: Double = 0.0,
    val durabilityHrs: Int = 100,
    val productionRatePerMin: Double = 1.0,
    val iconKey: String = "box"
)

object ProductCatalog {
    val BASE_PRODUCTS: List<Product> = listOf(
        // --- 1 to 15: Matérias-Primas Naturais ---
        Product("p_01", "Milho Bruto", ProductCategory.RAW_NATURAL, PhysicalState.SOLID, 720.0, "Baixa", 12.0, description = "Grão agrícola básico colhido para consumo ou processamento de farinha."),
        Product("p_02", "Soja", ProductCategory.RAW_NATURAL, PhysicalState.SOLID, 750.0, "Baixa", 16.5, description = "Oleaginosa rica em proteínas para alimentação animal e biocombustíveis."),
        Product("p_03", "Maçã Silvestre", ProductCategory.RAW_NATURAL, PhysicalState.SOLID, 600.0, "Média", 18.0, description = "Fruta fresca suculenta, base para sucos concentrados e cidras."),
        Product("p_04", "Laranja Valencia", ProductCategory.RAW_NATURAL, PhysicalState.SOLID, 650.0, "Média", 15.0, description = "Cítrico com alto teor de ácido ascórbico para sucos e néctares."),
        Product("p_05", "Cana-de-Açúcar", ProductCategory.RAW_NATURAL, PhysicalState.SOLID, 550.0, "Alta", 10.0, description = "Biomassa doce de alta produtividade para açúcar e bioetanol."),
        Product("p_06", "Trigo", ProductCategory.RAW_NATURAL, PhysicalState.SOLID, 780.0, "Baixa", 14.0, description = "Cereal dourado fundamental para moagens e panificação industrial."),
        Product("p_07", "Carne Bovina In Natura", ProductCategory.RAW_NATURAL, PhysicalState.SOLID, 950.0, "Média", 45.0, description = "Proteína animal de corte frigorificado para laticínios e charcutaria."),
        Product("p_08", "Leite Cru", ProductCategory.RAW_NATURAL, PhysicalState.LIQUID, 1030.0, "Média", 8.0, description = "Líquido biológico pasteurizável para queijos, manteigas e derivados."),
        Product("p_09", "Ovos de Granja", ProductCategory.RAW_NATURAL, PhysicalState.SOLID, 600.0, "Baixa", 6.5, description = "Proteína avícola básica em dúzias para alimentos processados."),
        Product("p_10", "Mel Puro", ProductCategory.RAW_NATURAL, PhysicalState.LIQUID, 1420.0, "Estável", 32.0, description = "Xarope orgânico com ação conservante natural coletado de apiários."),
        Product("p_11", "Minério de Ferro Bruto", ProductCategory.RAW_NATURAL, PhysicalState.SOLID, 4800.0, "Baixa", 22.0, description = "Rocha rica em óxidos de ferro extraída a céu aberto para siderurgia."),
        Product("p_12", "Carvão Mineral", ProductCategory.RAW_NATURAL, PhysicalState.SOLID, 1350.0, "Inflamável", 18.5, description = "Combustível fóssil rico em carbono para fornos industriais e termelétricas."),
        Product("p_13", "Silica em Pó", ProductCategory.RAW_NATURAL, PhysicalState.SOLID, 1600.0, "Inerte", 20.0, description = "Dióxido de silício puro para fabricação de vidros e chips semicondutores."),
        Product("p_14", "Petróleo Bruto", ProductCategory.RAW_NATURAL, PhysicalState.LIQUID, 860.0, "Altamente Inflamável", 55.0, description = "Ouro negro não refinado fóssil para fracionamento petroquímico."),
        Product("p_15", "Água de Aquífero", ProductCategory.RAW_NATURAL, PhysicalState.LIQUID, 1000.0, "Neutra", 4.0, description = "Água mineral subterrânea purificada para lavagem, eletrólise e consumo."),

        // --- 16 to 30: Insumos Energéticos e Básicos ---
        Product("p_16", "Eletricidade em Bateria", ProductCategory.ENERGY_BASIC, PhysicalState.SOLID, 2200.0, "Elétrica", 30.0, description = "MWh armazenado em células químicas para alimentação industrial."),
        Product("p_17", "Biogás Comprimido", ProductCategory.ENERGY_BASIC, PhysicalState.GAS, 1.2, "Comburente", 28.0, description = "Gás metano derivado da digestão anaeróbica de biomassa agrícola."),
        Product("p_18", "Calcário Moído", ProductCategory.ENERGY_BASIC, PhysicalState.SOLID, 1400.0, "Básica", 14.0, description = "Carbonato de cálcio para cimento, correção de solo e metalurgia."),
        Product("p_19", "Celulose Bruta", ProductCategory.ENERGY_BASIC, PhysicalState.SOLID, 800.0, "Orgânica", 25.0, description = "Fibra vegetal despolpada para papéis, embalagens e isolantes."),
        Product("p_20", "Óleo Vegetal Bruto", ProductCategory.ENERGY_BASIC, PhysicalState.LIQUID, 920.0, "Leve", 22.0, description = "Lípido prensado de soja e milho para culinária e biodiesel."),
        Product("p_21", "Farelo de Soja", ProductCategory.ENERGY_BASIC, PhysicalState.SOLID, 650.0, "Nutritiva", 19.0, description = "Resíduo prensado rico em aminoácidos para ração concentrada."),
        Product("p_22", "Farinha de Trigo Refinada", ProductCategory.ENERGY_BASIC, PhysicalState.SOLID, 580.0, "Inerte", 21.0, description = "Pó finamente moído e peneirado para massas alimentícias."),
        Product("p_23", "Açúcar Cristal", ProductCategory.ENERGY_BASIC, PhysicalState.SOLID, 850.0, "Doce", 18.0, description = "Sacarose purificada em cristais reluzentes da cana."),
        Product("p_24", "Álcool Etílico Hidratado", ProductCategory.ENERGY_BASIC, PhysicalState.LIQUID, 810.0, "Inflamável", 35.0, description = "Etanol destilado 96% para combustíveis e solventes sanitários."),
        Product("p_25", "Gasolina Refinada", ProductCategory.ENERGY_BASIC, PhysicalState.LIQUID, 740.0, "Volátil", 62.0, description = "Hidrocarboneto destilado de alta octanagem para veículos leves."),
        Product("p_26", "Óleo Diesel", ProductCategory.ENERGY_BASIC, PhysicalState.LIQUID, 840.0, "Inflamável", 58.0, description = "Fração média do petróleo com alto torque para frotas pesadas."),
        Product("p_27", "Gás Liquefeito (GLP)", ProductCategory.ENERGY_BASIC, PhysicalState.GAS, 2.0, "Pressurizado", 40.0, description = "Gás propano-butano liquefeito para caldeiras industriais."),
        Product("p_28", "Lingote de Ferro", ProductCategory.ENERGY_BASIC, PhysicalState.SOLID, 7870.0, "Oxidável", 48.0, description = "Bloco fundido de ferro gusa sólido para usinagem pesada."),
        Product("p_29", "Chapa de Aço Carbono", ProductCategory.ENERGY_BASIC, PhysicalState.SOLID, 7850.0, "Resistente", 85.0, description = "Aço laminado plano de alta resistência mecânica para chaparias."),
        Product("p_30", "Vidro Industrial Comum", ProductCategory.ENERGY_BASIC, PhysicalState.SOLID, 2500.0, "Quebradiço", 38.0, description = "Silicato plano transparente para janelas, frascos e estufas."),

        // --- 31 to 50: Componentes Mecânicos e Peças ---
        Product("p_31", "Parafusos e Fixadores de Aço", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 7800.0, "Mecânico", 28.0, description = "Elementos de rosca métrica para montagens estruturais."),
        Product("p_32", "Engrenagem de Cobre", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 8960.0, "Anti-faísca", 75.0, description = "Roda dentada de bronze/cobre com baixa fricção de engate."),
        Product("p_33", "Eixo de Transmissão", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 7850.0, "Torsão", 95.0, description = "Eixo usinado equilibrado para transferência de potência mecânica."),
        Product("p_34", "Correia de Borracha Dentada", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 1200.0, "Flexível", 42.0, description = "Correia síncrona vulcanizada para polias motrizes."),
        Product("p_35", "Mola de Alta Tensão", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 7700.0, "Elástica", 36.0, description = "Fita helicoidal temperada para absorção de impactos fortes."),
        Product("p_36", "Chassi Modular de Ferro", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 7800.0, "Estrutural", 210.0, description = "Armação esquelética reforçada para veículos e máquinas."),
        Product("p_37", "Bloco de Motor a Combustão", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 7200.0, "Térmico", 480.0, description = "Conjunto de cilindros e pistões a diesel de quatro tempos."),
        Product("p_38", "Motor Elétrico de Indução", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 6500.0, "Eletromagnético", 520.0, description = "Motor assíncrono trifásico de alto rendimento elétrico."),
        Product("p_39", "Placa de Circuito (PCB) Básica", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 1850.0, "Condutiva", 160.0, description = "Substrato em fibra de vidro com trilhas de cobre gravadas."),
        Product("p_40", "Fiação de Cobre Isolada", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 4500.0, "Dielétrica", 65.0, description = "Bobina de cabos flexíveis revestidos para chicotes elétricos."),
        Product("p_41", "Bateria de Íon-Lítio Padrão", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 2400.0, "Eletroquímica", 310.0, description = "Pack de células recarregáveis de alta densidade volumétrica."),
        Product("p_42", "Sistema Hidráulico de Pressão", ProductCategory.MECHANICAL_PARTS, PhysicalState.LIQUID, 1100.0, "Fluídico", 380.0, description = "Válvulas reguladoras e cilindros para empuxo mecânico pesado."),
        Product("p_43", "Bomba de Sucção de Alta Vazão", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 4200.0, "Hidráulico", 290.0, description = "Rotor centrífugo para bombeamento contínuo de fluidos e químicos."),
        Product("p_44", "Lâmina de Corte de Aço Inox", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 7900.0, "Afiada", 140.0, description = "Fio cirúrgico temperado resistente à oxidação para trituração."),
        Product("p_45", "Esteira Rolante Modular", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 2100.0, "Cinético", 230.0, description = "Segmentos intertravados para automação de linha fabril contínua."),
        Product("p_46", "Braço Robótico Simples", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 3200.0, "Cinemático", 850.0, description = "Manipulador articulado de 4 eixos para montagens de precisão."),
        Product("p_47", "Painel de Controle Analógico", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 1900.0, "Operacional", 175.0, description = "Mostradores, relés e botões de emergência para controle fabril."),
        Product("p_48", "Estrutura de Alumínio Aeronáutico", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 2700.0, "Ultraleve", 340.0, description = "Perfil tubular de liga 7075 com extrema relação resistência/peso."),
        Product("p_49", "Módulo de Refrigeração", ProductCategory.MECHANICAL_PARTS, PhysicalState.LIQUID, 1300.0, "Termodinâmico", 260.0, description = "Compressor com trocador de calor para câmaras frias."),
        Product("p_50", "Filtro de Ar Industrial", ProductCategory.MECHANICAL_PARTS, PhysicalState.SOLID, 400.0, "Porous", 90.0, description = "Membrana HEPA de microfibra para retenção de particulados finos."),

        // --- 51 to 70: Química Fina e Materiais Avançados ---
        Product("p_51", "Cobre Puro Refinado", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.SOLID, 8960.0, "Eletrocondutor", 120.0, description = "Cátodos 99.99% para alta eletrônica e bobinas eletromagnéticas."),
        Product("p_52", "Lingote de Ouro Fino", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.SOLID, 19300.0, "Nobre", 1850.0, description = "Metal nobre de reserva de valor e contatos elétricos inoxidáveis."),
        Product("p_53", "Barra de Prata Pura", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.SOLID, 10490.0, "Reflexiva", 420.0, description = "Maior condutividade térmica e elétrica entre todos os elementos."),
        Product("p_54", "Carbonato de Lítio Grau Bateria", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.SOLID, 2110.0, "Eletrolítico", 680.0, description = "Precursor refinado de sal para eletrólitos de super-baterias."),
        Product("p_55", "Óxido de Neodímio (Terras Raras)", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.SOLID, 7240.0, "Ferromagnético", 940.0, description = "Elemento crítico para ímãs permanentes de alta potência de motores."),
        Product("p_56", "Silício Poligranular Puro", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.SOLID, 2330.0, "Semicondutor", 390.0, description = "Grau 9N para crescimento de lingotes monocristalinos fotovoltaicos."),
        Product("p_57", "Polímero Termoplástico (PET/PE)", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.SOLID, 1380.0, "Polimérico", 70.0, description = "Resina plástica moldável para carcaças e garrafas leves."),
        Product("p_58", "Borracha Sintética Vulcanizada", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.SOLID, 1150.0, "Elastômero", 82.0, description = "Polímero elastômero reforçado para pneus de carga e vedantes."),
        Product("p_59", "Fertilizante NPK de Alta Eficiência", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.SOLID, 1250.0, "Nutritivo", 110.0, description = "Grânulos enriquecidos com Nitrogênio, Fósforo e Potássio."),
        Product("p_60", "Ácido Sulfúrico Concentrado", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.LIQUID, 1840.0, "Ácido Forte", 95.0, description = "Reagente fundamental para lixiviação de minérios e fertilizantes."),
        Product("p_61", "Solvente Orgânico Puro", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.LIQUID, 790.0, "Solúvel", 88.0, description = "Tolueno e acetona de alta pureza para desengraxe e vernizes."),
        Product("p_62", "Catalisador de Platina", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.SOLID, 21450.0, "Catalítico", 2900.0, description = "Partículas finas para reforma de hidrogênio e quebra molecular."),
        Product("p_63", "Resina Epóxi Estrutural", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.LIQUID, 1160.0, "Termorrígida", 130.0, description = "Matriz adesiva para compósitos de fibra de carbono aeroespaciais."),
        Product("p_64", "Fibra de Carbono Teclada", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.SOLID, 1750.0, "Tenaz", 450.0, description = "Tecido de alta rigidez específica para fuselagens e braços leves."),
        Product("p_65", "Cerâmica Técnica de Alumina", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.SOLID, 3950.0, "Isolante", 220.0, description = "Refratário ultra-resistente ao desgaste para bocais e isoladores."),
        Product("p_66", "Gás Hidrogênio Verde Criogênico", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.LIQUID, 71.0, "Criogênico", 320.0, description = "Combustível de emissão zero produzido por eletrólise da água."),
        Product("p_67", "Oxigênio Líquido Puro (LOX)", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.LIQUID, 1141.0, "Comburente Extremo", 180.0, description = "Oxidante criogênico essencial para propulsão de foguetes."),
        Product("p_68", "Querosene de Aviação Sintético", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.LIQUID, 800.0, "Inflamável", 195.0, description = "Jet A-1 puro sem enxofre sintetizado via rota Fischer-Tropsch."),
        Product("p_69", "Gás Argônio de Soldagem", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.GAS, 1.78, "Nobre Inerte", 85.0, description = "Atmosfera protetora para solda TIG e corte a laser de titânio."),
        Product("p_70", "Gel Refrigerante Supercondutor", ProductCategory.ADVANCED_CHEMICAL, PhysicalState.LIQUID, 980.0, "Térmico Especial", 540.0, description = "Fluido de troca térmica para bobinas magnéticas de fusão."),

        // --- 71 to 100: Alta Tecnologia, Automação & Espaço ---
        Product("p_71", "Microprocessador de 32 Bits", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 2300.0, "Lógica", 620.0, description = "Unidade central de processamento para CLPs industriais."),
        Product("p_72", "Processador de IA Neural 5nm", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 2400.0, "Quântico/Lógico", 2400.0, description = "Chip especializado em inferência de redes neurais e visão robótica."),
        Product("p_73", "Sensor Quântico de Pressão", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 1950.0, "Sensorial", 890.0, description = "Transdutor piezo-óptico com sensibilidade de micropascal."),
        Product("p_74", "Servomotor Brushless de Precisão", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 4800.0, "Eletromecânico", 780.0, description = "Motor com encoder ótico integrado para robótica de precisão."),
        Product("p_75", "Módulo de Bateria de Grafeno", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 1900.0, "Supercondutiva", 1600.0, description = "Carga ultra-rápida em 90 segundos com 50.000 ciclos úteis."),
        Product("p_76", "Chassi de Titânio Blindado", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 4510.0, "Balístico", 1350.0, description = "Liga Ti-6Al-4V imune à corrosão e impacto de micrometeoritos."),
        Product("p_77", "Painel Solar Espacial Perovskita", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 1500.0, "Fotovoltaico", 980.0, description = "Eficiência de 32% com resistência a radiação cósmica pesada."),
        Product("p_78", "Supercondutor de Alta Temperatura", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 6300.0, "Resistência Zero", 3100.0, description = "Fita YBCO para levitação magnética e reatores de fusão."),
        Product("p_79", "Escudo Térmico Cerâmico Ablativo", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 1800.0, "Ablativo", 1850.0, description = "Placas de zircônia para suportar reentrada atmosférica a 2000°C."),
        Product("p_80", "Traje Extraveicular Hermético", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 1200.0, "Vida Autônoma", 4200.0, description = "Suporte de vida para 14 horas de trabalho no vácuo estelar."),
        Product("p_81", "Computador de Bordo Aeroespacial", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 2100.0, "Rad-Hard", 5500.0, description = "Sistemas tolerantes a falhas triplas para guiagem de foguetes."),
        Product("p_82", "Satélite de Comunicação Micro-LEO", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 1600.0, "Transmissor", 18000.0, description = "Constelação orbital de banda larga para telemetria planetária."),
        Product("p_83", "Droide Autônomo de Mineração", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 3400.0, "IA Autônoma", 26000.0, description = "Robô bípede com perfuratriz a laser para rochas extraterrestres."),
        Product("p_84", "Módulo Habitacional Hermético", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 2100.0, "Bio-estrutural", 75000.0, description = "Célula expansível com atmosfera controlada para colônias."),
        Product("p_85", "Minério de Platina Asteroidal", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 21450.0, "Supernobre", 12500.0, description = "Nódulo metálico colhido de asteroide tipo M sem gravidade."),
        Product("p_86", "Água Lunar Criogênica Filtrada", ProductCategory.HIGH_TECH_SPACE, PhysicalState.LIQUID, 1000.0, "Pura", 1200.0, description = "Gelo de cratera lunar sublimado e condensado para propelente."),
        Product("p_87", "Regolito Lunar Sinterizado", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 2800.0, "Basáltico", 650.0, description = "Concreto extraterrestre obtido por fusão micro-ondas de poeira."),
        Product("p_88", "Gerador de Fusão Compacto", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 8500.0, "Termonuclear", 140000.0, description = "Reator D-T miniatura gerando 50 MW contínuos limpos."),
        Product("p_89", "Hélio-3 Enriquecido (Isótopo)", ProductCategory.HIGH_TECH_SPACE, PhysicalState.GAS, 0.18, "Nuclear Limpo", 85000.0, description = "Combustível de fusão aneutrônica colhido no regolito lunar."),
        Product("p_90", "Propulsor Iônico Efeito Hall", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 3200.0, "Impulso Específico", 48000.0, description = "Acelerador eletrostático de xenônio para viagens interplanetárias."),
        Product("p_91", "Cúpula Bioclimática Marciana", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 1900.0, "Ecossistema", 110000.0, description = "Estufa selada de polímero com ciclo fechado de carbono e água."),
        Product("p_92", "Estação de Recarga Orbital", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 4500.0, "Doca Espacial", 320000.0, description = "Depósito autônomo de combustíveis criogênicos em órbita baixa."),
        Product("p_93", "Antimatéria Confinada (Picogramas)", ProductCategory.HIGH_TECH_SPACE, PhysicalState.PLASMA, 0.001, "Aniquilação", 950000.0, description = "Posítrons aprisionados em armadilha Penning de vácuo extremo."),
        Product("p_94", "Lente Gravitacional Espelhada", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 2600.0, "Óptica Cósmica", 62000.0, description = "Espelho parabólico espacial para transmissão de energia por laser."),
        Product("p_95", "Fibra Óptica Monomodo Quântica", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 2200.0, "Emaranhamento", 480.0, description = "Guia de onda em sílica com perdas inferiores a 0.1 dB/km."),
        Product("p_96", "Lâmina de Plasma Térmico", ProductCategory.HIGH_TECH_SPACE, PhysicalState.PLASMA, 0.01, "Corte Atômico", 3600.0, description = "Tocha de arco estabilizada por campo para corte de ligas estelares."),
        Product("p_97", "Módulo de Hidroponia Orbital", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 1400.0, "Bio-regenerativo", 8400.0, description = "Câmara aeropônica rotativa para cultivo em microgravidade."),
        Product("p_98", "Robô Cirúrgico Autónomo", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 2800.0, "Biomédico", 38000.0, description = "Braços microscópicos com telepresença e IA de sutura."),
        Product("p_99", "Módulo de Criptografia Quântica", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 1650.0, "QKD Inviolável", 14500.0, description = "Chaves quânticas em tempo real para redes financeiras globais."),
        Product("p_100", "Veículo de Lançamento Suborbital", ProductCategory.HIGH_TECH_SPACE, PhysicalState.SOLID, 12000.0, "Propulsão Mista", 210000.0, description = "Foguete reutilizável de primeiro estágio com pouso autônomo.")
    )

    fun findById(id: String): Product? = BASE_PRODUCTS.find { it.id == id }
}
