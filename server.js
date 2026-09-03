// ============================================================================
// MAGNATA TYCOON ONLINE - SERVER ENGINE (Node.js + Express + WebSockets)
// ============================================================================
const express = require('express');
const http = require('http');
const { WebSocketServer, WebSocket } = require('ws');
const path = require('path');
const fs = require('fs');

const app = express();
const server = http.createServer(app);
const wss = new WebSocketServer({ server });

const PORT = process.env.APP_PORT || 3000;
const GEMINI_API_KEY = process.env.GEMINI_API_KEY || '';

app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

// ----------------------------------------------------------------------------
// 1. DATA CATALOGS & MACRO SIMULATION ENGINE
// ----------------------------------------------------------------------------
const ERAS = [
  { id: 1, name: 'Fazenda de Subsistência', targetNetWorth: 0, icon: '🌾', color: '#10b981', desc: 'Produção manual, agricultura básica, moinhos rústicos e escambo local.' },
  { id: 2, name: 'Agroindústria Local', targetNetWorth: 50000, icon: '🚜', color: '#f59e0b', desc: 'Silos de grãos, mecanização pesada, adubos químicos e transporte rodoviário.' },
  { id: 3, name: 'Polo Industrial Urbano', targetNetWorth: 500000, icon: '🏭', color: '#3b82f6', desc: 'Usinas siderúrgicas, refinarias petroquímicas, linhas de montagem e ferrovias.' },
  { id: 4, name: 'Conglomerado Tecnológico', targetNetWorth: 5000000, icon: '⚡', color: '#8b5cf6', desc: 'Microchips quânticos, robótica avançada, logística autônoma e inteligência preditiva.' },
  { id: 5, name: 'Corporação Espacial', targetNetWorth: 50000000, icon: '🚀', color: '#ec4899', desc: 'Mineração em asteroides, refinarias orbitais de Hélio-3 e colônias em Marte.' }
];

const BASE_COMMODITIES = [
  { id: 'trigo', name: 'Trigo Orgânico', era: 1, basePrice: 12, category: 'Agrícola', icon: '🌾' },
  { id: 'leite', name: 'Leite Cru', era: 1, basePrice: 18, category: 'Agrícola', icon: '🥛' },
  { id: 'madeira', name: 'Madeira Tratada', era: 1, basePrice: 25, category: 'Matéria-Prima', icon: '🪵' },
  { id: 'farinha', name: 'Farinha Refinada', era: 1, basePrice: 35, category: 'Processado', icon: '🥖' },
  { id: 'queijo_artesanal', name: 'Queijo Artesanal', era: 2, basePrice: 65, category: 'Processado', icon: '🧀' },
  { id: 'adubo_bio', name: 'Biofertilizante NPK', era: 2, basePrice: 90, category: 'Químico', icon: '🧪' },
  { id: 'graos_soja', name: 'Soja Transgênica', era: 2, basePrice: 110, category: 'Agrícola', icon: '🌱' },
  { id: 'oleo_vegetal', name: 'Óleo Industrial', era: 2, basePrice: 150, category: 'Processado', icon: '🛢️' },
  { id: 'ferro_gusa', name: 'Lingote de Ferro', era: 3, basePrice: 280, category: 'Metalúrgico', icon: '⛓️' },
  { id: 'aco_estrutural', name: 'Viga de Aço Forjado', era: 3, basePrice: 480, category: 'Metalúrgico', icon: '🏗️' },
  { id: 'polimero_sintetico', name: 'Polímero Sintético', era: 3, basePrice: 410, category: 'Químico', icon: '🧬' },
  { id: 'engrenagens_mecanicas', name: 'Conjunto Mecânico', era: 3, basePrice: 650, category: 'Manufatura', icon: '⚙️' },
  { id: 'silicio_grau_solar', name: 'Silício Cristalino', era: 4, basePrice: 1250, category: 'Eletrônico', icon: '💎' },
  { id: 'microprocessador', name: 'Processador Neural', era: 4, basePrice: 2800, category: 'Eletrônico', icon: '💻' },
  { id: 'bateria_grafeno', name: 'Célula de Grafeno', era: 4, basePrice: 3900, category: 'Energia', icon: '🔋' },
  { id: 'servomotor_ia', name: 'Atuador Robótico IA', era: 4, basePrice: 5400, category: 'Robótica', icon: '🦾' },
  { id: 'liga_titânio_aero', name: 'Superliga Aeroespacial', era: 5, basePrice: 11500, category: 'Espacial', icon: '🛡️' },
  { id: 'propulsor_ionico', name: 'Propulsor Iônico Suborbital', era: 5, basePrice: 28000, category: 'Espacial', icon: '🚀' },
  { id: 'helio_3_puro', name: 'Isótopo Hélio-3 Comprimido', era: 5, basePrice: 62000, category: 'Combustível', icon: '🔮' },
  { id: 'nucleo_antimateria', name: 'Célula de Contenção de Plasma', era: 5, basePrice: 145000, category: 'Exótico', icon: '🌌' }
];

const BASE_MACHINES = [
  { id: 'm_horta', name: 'Canteiro Agroecológico', era: 1, cost: 800, power: 0, outputRate: 4, inputId: null, outputId: 'trigo', icon: '🌱' },
  { id: 'm_moinho', name: 'Moinho de Pedra', era: 1, cost: 2500, power: 2, outputRate: 3, inputId: 'trigo', outputId: 'farinha', icon: '🏛️' },
  { id: 'm_estufa', name: 'Estufa Hidropônica V1', era: 2, cost: 12000, power: 8, outputRate: 6, inputId: 'adubo_bio', outputId: 'graos_soja', icon: '🏡' },
  { id: 'm_silo', name: 'Silo e Prensa de Óleo', era: 2, cost: 28000, power: 15, outputRate: 5, inputId: 'graos_soja', outputId: 'oleo_vegetal', icon: '🏭' },
  { id: 'm_alto_forno', name: 'Alto-Forno Siderúrgico', era: 3, cost: 120000, power: 45, outputRate: 4, inputId: 'ferro_gusa', outputId: 'aco_estrutural', icon: '🔥' },
  { id: 'm_torno_cnc', name: 'Usinagem de Precisão CNC', era: 3, cost: 240000, power: 60, outputRate: 3, inputId: 'aco_estrutural', outputId: 'engrenagens_mecanicas', icon: '⚙️' },
  { id: 'm_sala_limpa', name: 'Litografia de Semicondutores', era: 4, cost: 1200000, power: 180, outputRate: 2, inputId: 'silicio_grau_solar', outputId: 'microprocessador', icon: '🔬' },
  { id: 'm_fabrica_baterias', name: 'Sintetizador de Baterias', era: 4, cost: 2800000, power: 250, outputRate: 2, inputId: 'polimero_sintetico', outputId: 'bateria_grafeno', icon: '⚡' },
  { id: 'm_fundicao_vácuo', name: 'Forja Orbital em Gravidade Zero', era: 5, cost: 15000000, power: 800, outputRate: 1.5, inputId: 'liga_titânio_aero', outputId: 'propulsor_ionico', icon: '🛰️' },
  { id: 'm_reator_fusao', name: 'Reator Tokamak Orbital', era: 5, cost: 45000000, power: -2500, outputRate: 1, inputId: 'helio_3_puro', outputId: 'nucleo_antimateria', icon: '⚛️' }
];

// In-memory Global State
const gameState = {
  activeUsers: 48,
  serverTick: 0,
  economicEvent: {
    title: 'Estabilidade Econômica Global',
    desc: 'Os mercados operam sob condições normais de liquidez e demanda equilibrada.',
    impactItem: 'trigo',
    multiplier: 1.0,
    remainingSeconds: 60
  },
  orderBook: {}, // itemId -> { bids: [{id, price, amount, user, timestamp}], asks: [{id, price, amount, user, timestamp}], history: [] }
  marketPrices: {},
  chatMessages: [
    { id: 1, user: 'SiderúrgicaNacional', text: 'Compro 200 lingotes de aço a $460 cada! Respondam com contrato direto.', time: '12:04' },
    { id: 2, user: 'AgroSul_Log', text: 'Vendendo 500 sacas de trigo orgânico. Preço especial para atacadistas.', time: '12:05' },
    { id: 3, user: 'NovaSat_Orbital', text: 'Buscando fornecedores contínuos de baterias de grafeno para constelação satelital.', time: '12:06' }
  ],
  activeContracts: [
    { id: 'CTR-101', from: 'AgroSul_Log', to: 'Mercado Livre', item: 'trigo', amount: 300, pricePerUnit: 11.5, total: 3450, status: 'OPEN' },
    { id: 'CTR-102', from: 'NovaSat_Orbital', to: 'Qualquer Magnata', item: 'bateria_grafeno', amount: 50, pricePerUnit: 3850, total: 192500, status: 'OPEN' }
  ],
  leaderboard: [
    { rank: 1, name: 'AeroDynasty Corp', era: 'Corporação Espacial', netWorth: 184500900, rating: 'AAA' },
    { rank: 2, name: 'Vortex Quantum Ind.', era: 'Conglomerado Tecnológico', netWorth: 67200400, rating: 'AA+' },
    { rank: 3, name: 'Siderúrgica Titan', era: 'Polo Industrial', netWorth: 24150000, rating: 'A+' },
    { rank: 4, name: 'AgroMega Brasil', era: 'Agroindústria Local', netWorth: 4320000, rating: 'BBB' },
    { rank: 5, name: 'Sua Corporação', era: 'Fazenda de Subsistência', netWorth: 25000, rating: 'B' }
  ],
  inventedItems: []
};

// Initialize order book and market prices
BASE_COMMODITIES.forEach(item => {
  gameState.marketPrices[item.id] = {
    price: item.basePrice,
    change24h: ((Math.random() * 6) - 2.8).toFixed(1),
    volume: Math.floor(item.basePrice * (50 + Math.random() * 200)),
    history: [item.basePrice * 0.96, item.basePrice * 0.98, item.basePrice * 1.02, item.basePrice]
  };

  const p = item.basePrice;
  gameState.orderBook[item.id] = {
    bids: [
      { id: `b1_${item.id}`, price: +(p * 0.95).toFixed(1), amount: Math.floor(20 + Math.random() * 80), user: 'AlphaBot_P2P' },
      { id: `b2_${item.id}`, price: +(p * 0.91).toFixed(1), amount: Math.floor(40 + Math.random() * 150), user: 'TraderFundo_IX' },
      { id: `b3_${item.id}`, price: +(p * 0.88).toFixed(1), amount: Math.floor(100 + Math.random() * 300), user: 'MarketMaker_Pro' }
    ],
    asks: [
      { id: `a1_${item.id}`, price: +(p * 1.05).toFixed(1), amount: Math.floor(15 + Math.random() * 70), user: 'GlobalCommodities' },
      { id: `a2_${item.id}`, price: +(p * 1.09).toFixed(1), amount: Math.floor(50 + Math.random() * 120), user: 'EstoqueCentral' },
      { id: `a3_${item.id}`, price: +(p * 1.14).toFixed(1), amount: Math.floor(80 + Math.random() * 250), user: 'MegaLogistics' }
    ],
    history: [
      { id: 'tx_1', price: +(p * 1.01).toFixed(1), amount: 40, time: '11:45', buyer: 'TraderFundo_IX', seller: 'GlobalCommodities' },
      { id: 'tx_2', price: +(p * 0.99).toFixed(1), amount: 65, time: '11:58', buyer: 'AlphaBot_P2P', seller: 'MegaLogistics' }
    ]
  };
});

// Single player session simulation (saved & auto-synced)
let playerProfile = {
  name: 'Você (Magnata)',
  eraId: 1,
  cash: 18500,
  powerGrid: { total: 20, used: 2 },
  warehouse: {
    capacity: 2500,
    used: 180,
    inventory: {
      trigo: 120,
      farinha: 40,
      madeira: 20
    }
  },
  fleet: [
    { id: 'v1', name: 'Caminhonete Rural 4x4', capacity: 300, speed: 'Rápido', status: 'LIVRE', count: 2 },
    { id: 'v2', name: 'Caminhão Baú Rodoviário', capacity: 1500, speed: 'Médio', status: 'LIVRE', count: 1 }
  ],
  factoryPlots: [
    { plotId: 0, machineId: 'm_horta', name: 'Canteiro Agroecológico', level: 2, active: true, progress: 0.65 },
    { plotId: 1, machineId: 'm_moinho', name: 'Moinho de Pedra', level: 1, active: true, progress: 0.35 },
    { plotId: 2, machineId: null, name: 'Lote Vazio', level: 0, active: false, progress: 0 },
    { plotId: 3, machineId: null, name: 'Lote Vazio', level: 0, active: false, progress: 0 },
    { plotId: 4, machineId: null, name: 'Lote Vazio', level: 0, active: false, progress: 0 },
    { plotId: 5, machineId: null, name: 'Lote Vazio', level: 0, active: false, progress: 0 },
    { plotId: 6, machineId: null, name: 'Lote Vazio', level: 0, active: false, progress: 0 },
    { plotId: 7, machineId: null, name: 'Lote Vazio', level: 0, active: false, progress: 0 }
  ],
  patents: []
};

// ----------------------------------------------------------------------------
// 2. ECONOMIC SIMULATION & TICK ENGINE (Every 3 seconds)
// ----------------------------------------------------------------------------
const ECONOMIC_EVENTS_POOL = [
  { title: 'Seca Severa no Centro-Sul', desc: 'Produção agrícola reduzida globalmente. Cotação de grãos e trigo em alta acentuada (+35%).', item: 'trigo', mult: 1.35 },
  { title: 'Avanço em Fusão a Frio', desc: 'Descoberta quântica reduz custos de energia e impulsiona demanda por microchips (+28%).', item: 'microprocessador', mult: 1.28 },
  { title: 'Superávit Minério de Ferro', desc: 'Novas jazidas descobertas. Preço do aço e lingotes recua ligeiramente (-15%).', item: 'aco_estrutural', mult: 0.85 },
  { title: 'Boom de Corrida Espacial', desc: 'Novas colônias lunares impulsionam encomendas governamentais de propulsores iônicos (+45%).', item: 'propulsor_ionico', mult: 1.45 },
  { title: 'Inovação em Baterias Quânticas', desc: 'Alta procura de veículos elétricos e cargueiros por células de grafeno (+22%).', item: 'bateria_grafeno', mult: 1.22 }
];

setInterval(() => {
  gameState.serverTick++;

  // 1. Update Economic Event countdown
  gameState.economicEvent.remainingSeconds -= 3;
  if (gameState.economicEvent.remainingSeconds <= 0) {
    const nextEvent = ECONOMIC_EVENTS_POOL[Math.floor(Math.random() * ECONOMIC_EVENTS_POOL.length)];
    gameState.economicEvent = {
      title: nextEvent.title,
      desc: nextEvent.desc,
      impactItem: nextEvent.item,
      multiplier: nextEvent.mult,
      remainingSeconds: 75
    };
    broadcast({ type: 'EVENT_ALERT', event: gameState.economicEvent });
  }

  // 2. Micro-fluctuations on Order Book & Prices
  BASE_COMMODITIES.forEach(item => {
    const ob = gameState.orderBook[item.id];
    const mp = gameState.marketPrices[item.id];
    if (!ob || !mp) return;

    let targetPrice = item.basePrice;
    if (gameState.economicEvent.impactItem === item.id) {
      targetPrice *= gameState.economicEvent.multiplier;
    }

    // Dynamic noise
    const delta = (Math.random() - 0.49) * (targetPrice * 0.025);
    mp.price = Math.max(1, +(mp.price + delta).toFixed(2));

    // Update history
    mp.history.push(mp.price);
    if (mp.history.length > 20) mp.history.shift();
    mp.change24h = (((mp.price - item.basePrice) / item.basePrice) * 100).toFixed(1);

    // Bot automated orders
    if (Math.random() > 0.6) {
      const isBuy = Math.random() > 0.5;
      const botP = +(mp.price * (isBuy ? 0.98 : 1.02)).toFixed(1);
      const botAmt = Math.floor(5 + Math.random() * 25);
      const targetList = isBuy ? ob.bids : ob.asks;
      
      targetList.unshift({
        id: 'ord_' + Math.random().toString(36).substring(2, 7),
        price: botP,
        amount: botAmt,
        user: isBuy ? 'Fundo_Quantum' : 'Cooperativa_Livre'
      });
      if (targetList.length > 7) targetList.pop();
    }
  });

  // 3. Factory production tick for player
  let powerConsumed = 0;
  let totalWarehouseUsed = 0;
  Object.values(playerProfile.warehouse.inventory).forEach(amt => totalWarehouseUsed += amt);

  playerProfile.factoryPlots.forEach(plot => {
    if (!plot.machineId || !plot.active) return;
    const machine = BASE_MACHINES.find(m => m.id === plot.machineId) || gameState.inventedItems.find(i => i.id === plot.machineId);
    if (!machine) return;

    powerConsumed += (machine.power || 0) * plot.level;

    // Check capacity
    if (totalWarehouseUsed < playerProfile.warehouse.capacity) {
      plot.progress += 0.25 * plot.level;
      if (plot.progress >= 1.0) {
        plot.progress = 0;
        // Check input requirement
        if (machine.inputId) {
          const currentInput = playerProfile.warehouse.inventory[machine.inputId] || 0;
          if (currentInput >= 1) {
            playerProfile.warehouse.inventory[machine.inputId] -= 1;
            playerProfile.warehouse.inventory[machine.outputId] = (playerProfile.warehouse.inventory[machine.outputId] || 0) + (machine.outputRate || 2);
          }
        } else {
          // Autonomous generation (e.g. horta, reator)
          playerProfile.warehouse.inventory[machine.outputId] = (playerProfile.warehouse.inventory[machine.outputId] || 0) + (machine.outputRate || 2);
        }
      }
    }
  });

  playerProfile.powerGrid.used = Math.max(0, powerConsumed);
  playerProfile.warehouse.used = totalWarehouseUsed;

  // Calculate Net Worth for Player
  let inventoryValue = 0;
  Object.entries(playerProfile.warehouse.inventory).forEach(([itemId, qty]) => {
    const pr = gameState.marketPrices[itemId]?.price || 10;
    inventoryValue += qty * pr;
  });
  const currentNetWorth = Math.floor(playerProfile.cash + inventoryValue);

  // Update Player's Rank in Leaderboard
  const userRankEntry = gameState.leaderboard.find(l => l.name === 'Sua Corporação');
  if (userRankEntry) {
    userRankEntry.netWorth = currentNetWorth;
    const eraObj = ERAS.find(e => e.id === playerProfile.eraId);
    if (eraObj) userRankEntry.era = eraObj.name;
  }
  gameState.leaderboard.sort((a, b) => b.netWorth - a.netWorth);
  gameState.leaderboard.forEach((item, idx) => item.rank = idx + 1);

  // Broadcast sync to all clients
  broadcast({
    type: 'TICK',
    tick: gameState.serverTick,
    marketPrices: gameState.marketPrices,
    economicEvent: gameState.economicEvent,
    playerSync: {
      cash: playerProfile.cash,
      netWorth: currentNetWorth,
      inventory: playerProfile.warehouse.inventory,
      warehouseUsed: totalWarehouseUsed,
      powerUsed: powerConsumed,
      plots: playerProfile.factoryPlots
    }
  });
}, 3000);

// ----------------------------------------------------------------------------
// 3. WEBSOCKET REAL-TIME DISPATCHER
// ----------------------------------------------------------------------------
function broadcast(msg) {
  const payload = JSON.stringify(msg);
  wss.clients.forEach(client => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(payload);
    }
  });
}

wss.on('connection', ws => {
  gameState.activeUsers++;
  ws.send(JSON.stringify({
    type: 'INIT',
    state: {
      eras: ERAS,
      commodities: BASE_COMMODITIES,
      machines: BASE_MACHINES,
      inventedItems: gameState.inventedItems,
      marketPrices: gameState.marketPrices,
      orderBook: gameState.orderBook,
      chatMessages: gameState.chatMessages,
      activeContracts: gameState.activeContracts,
      leaderboard: gameState.leaderboard,
      economicEvent: gameState.economicEvent,
      player: playerProfile
    }
  }));

  ws.on('message', data => {
    try {
      const msg = JSON.parse(data);
      handleClientMessage(ws, msg);
    } catch (e) {
      console.error('Failed to parse client message:', e);
    }
  });

  ws.on('close', () => {
    gameState.activeUsers = Math.max(1, gameState.activeUsers - 1);
  });
});

function handleClientMessage(ws, msg) {
  if (msg.type === 'CHAT_MESSAGE') {
    const newMsg = {
      id: Date.now(),
      user: playerProfile.name,
      text: String(msg.text).substring(0, 180),
      time: new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })
    };
    gameState.chatMessages.push(newMsg);
    if (gameState.chatMessages.length > 50) gameState.chatMessages.shift();
    broadcast({ type: 'NEW_CHAT', message: newMsg });

    // Bot response after short delay if asking to buy/sell
    if (newMsg.text.toLowerCase().includes('compro') || newMsg.text.toLowerCase().includes('vendo')) {
      setTimeout(() => {
        const botReply = {
          id: Date.now() + 1,
          user: 'InterTrade_Bot',
          text: `Proposta de @${playerProfile.name} registrada no Livro P2P. Verifique a aba de Contratos!`,
          time: new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })
        };
        gameState.chatMessages.push(botReply);
        broadcast({ type: 'NEW_CHAT', message: botReply });
      }, 1500);
    }
  }
}

// ----------------------------------------------------------------------------
// 4. REST API ENDPOINTS
// ----------------------------------------------------------------------------

// Get Complete State
app.get('/api/state', (req, res) => {
  res.json({
    eras: ERAS,
    commodities: BASE_COMMODITIES,
    machines: BASE_MACHINES,
    inventedItems: gameState.inventedItems,
    marketPrices: gameState.marketPrices,
    orderBook: gameState.orderBook,
    chatMessages: gameState.chatMessages,
    activeContracts: gameState.activeContracts,
    leaderboard: gameState.leaderboard,
    economicEvent: gameState.economicEvent,
    player: playerProfile
  });
});

// P2P Order Placement (Limit / Market)
app.post('/api/order/create', (req, res) => {
  const { itemId, side, price, amount } = req.body;
  const numAmount = parseInt(amount, 10);
  const numPrice = parseFloat(price);

  if (!itemId || !side || isNaN(numAmount) || numAmount <= 0 || isNaN(numPrice) || numPrice <= 0) {
    return res.status(400).json({ error: 'Parâmetros de ordem inválidos.' });
  }

  const ob = gameState.orderBook[itemId];
  if (!ob) return res.status(404).json({ error: 'Mercadoria não encontrada no mercado.' });

  const totalValue = numPrice * numAmount;

  if (side === 'BUY') {
    if (playerProfile.cash < totalValue) {
      return res.status(400).json({ error: `Saldo insuficiente. Necessário: $${totalValue.toFixed(2)}, disponível: $${playerProfile.cash.toFixed(2)}` });
    }
    // Check instant execution with asks
    let remaining = numAmount;
    let cost = 0;
    while (ob.asks.length > 0 && ob.asks[0].price <= numPrice && remaining > 0) {
      const topAsk = ob.asks[0];
      const tradeQty = Math.min(topAsk.amount, remaining);
      cost += tradeQty * topAsk.price;
      topAsk.amount -= tradeQty;
      remaining -= tradeQty;

      // Add to player warehouse
      playerProfile.warehouse.inventory[itemId] = (playerProfile.warehouse.inventory[itemId] || 0) + tradeQty;
      ob.history.unshift({
        id: 'tx_' + Date.now(),
        price: topAsk.price,
        amount: tradeQty,
        time: new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' }),
        buyer: 'Você',
        seller: topAsk.user
      });
      if (topAsk.amount <= 0) ob.asks.shift();
    }

    playerProfile.cash -= cost;

    // Place remaining on book if any
    if (remaining > 0) {
      const orderDeposit = remaining * numPrice;
      playerProfile.cash -= orderDeposit;
      ob.bids.push({
        id: 'usr_b_' + Date.now(),
        price: numPrice,
        amount: remaining,
        user: 'Você (P2P)'
      });
      ob.bids.sort((a, b) => b.price - a.price);
    }

    broadcast({ type: 'ORDER_BOOK_UPDATE', itemId, orderBook: ob });
    return res.json({ success: true, message: 'Ordem de compra processada com sucesso!', player: playerProfile });
  } 
  else if (side === 'SELL') {
    const currentStock = playerProfile.warehouse.inventory[itemId] || 0;
    if (currentStock < numAmount) {
      return res.status(400).json({ error: `Estoque insuficiente em armazém. Você possui ${currentStock} unidades.` });
    }

    let remaining = numAmount;
    let earned = 0;

    while (ob.bids.length > 0 && ob.bids[0].price >= numPrice && remaining > 0) {
      const topBid = ob.bids[0];
      const tradeQty = Math.min(topBid.amount, remaining);
      earned += tradeQty * topBid.price;
      topBid.amount -= tradeQty;
      remaining -= tradeQty;

      playerProfile.warehouse.inventory[itemId] -= tradeQty;
      ob.history.unshift({
        id: 'tx_' + Date.now(),
        price: topBid.price,
        amount: tradeQty,
        time: new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' }),
        buyer: topBid.user,
        seller: 'Você'
      });
      if (topBid.amount <= 0) ob.bids.shift();
    }

    playerProfile.cash += earned;

    if (remaining > 0) {
      playerProfile.warehouse.inventory[itemId] -= remaining;
      ob.asks.push({
        id: 'usr_a_' + Date.now(),
        price: numPrice,
        amount: remaining,
        user: 'Você (P2P)'
      });
      ob.asks.sort((a, b) => a.price - b.price);
    }

    broadcast({ type: 'ORDER_BOOK_UPDATE', itemId, orderBook: ob });
    return res.json({ success: true, message: 'Ordem de venda executada/listada no livro!', player: playerProfile });
  }

  res.status(400).json({ error: 'Operação inválida.' });
});

// P2P Direct Contract Creation
app.post('/api/contract/create', (req, res) => {
  const { to, item, amount, pricePerUnit } = req.body;
  if (!item || !amount || !pricePerUnit) {
    return res.status(400).json({ error: 'Dados incompletos para o contrato comercial.' });
  }

  const contract = {
    id: 'CTR-' + Math.floor(100 + Math.random() * 900),
    from: playerProfile.name,
    to: to || 'Mercado Geral',
    item,
    amount: parseInt(amount, 10),
    pricePerUnit: parseFloat(pricePerUnit),
    total: parseInt(amount, 10) * parseFloat(pricePerUnit),
    status: 'OPEN'
  };

  gameState.activeContracts.unshift(contract);
  broadcast({ type: 'NEW_CONTRACT', contract });
  res.json({ success: true, contract });
});

// Accept / Fulfill Contract
app.post('/api/contract/accept', (req, res) => {
  const { contractId } = req.body;
  const contract = gameState.activeContracts.find(c => c.id === contractId);
  if (!contract || contract.status !== 'OPEN') {
    return res.status(404).json({ error: 'Contrato não encontrado ou já encerrado.' });
  }

  // Fulfill contract: buyer pays total, gets item
  if (playerProfile.cash < contract.total) {
    return res.status(400).json({ error: `Saldo insuficiente para liquidar este contrato ($${contract.total.toFixed(2)}).` });
  }

  playerProfile.cash -= contract.total;
  playerProfile.warehouse.inventory[contract.item] = (playerProfile.warehouse.inventory[contract.item] || 0) + contract.amount;
  contract.status = 'COMPLETED';

  broadcast({ type: 'CONTRACT_UPDATED', contract });
  res.json({ success: true, message: `Contrato ${contract.id} liquidado com sucesso! Mercadorias recebidas.`, player: playerProfile });
});

// Place Machine on Factory Grid
app.post('/api/factory/place', (req, res) => {
  const { plotId, machineId } = req.body;
  if (plotId === undefined || !machineId) {
    return res.status(400).json({ error: 'Lote ou máquina inválida.' });
  }

  const machine = BASE_MACHINES.find(m => m.id === machineId) || gameState.inventedItems.find(i => i.id === machineId);
  if (!machine) return res.status(404).json({ error: 'Maquinário não registrado no catálogo industrial.' });

  if (playerProfile.cash < machine.cost) {
    return res.status(400).json({ error: `Saldo insuficiente. Custo: $${machine.cost}, disponível: $${playerProfile.cash}` });
  }

  playerProfile.cash -= machine.cost;
  playerProfile.factoryPlots[plotId] = {
    plotId,
    machineId,
    name: machine.name,
    level: 1,
    active: true,
    progress: 0
  };

  res.json({ success: true, message: `${machine.name} instalado no lote ${plotId + 1}!`, player: playerProfile });
});

// Upgrade Machine
app.post('/api/factory/upgrade', (req, res) => {
  const { plotId } = req.body;
  const plot = playerProfile.factoryPlots[plotId];
  if (!plot || !plot.machineId) {
    return res.status(400).json({ error: 'Lote vazio ou inexistente.' });
  }

  const upgradeCost = Math.floor(1500 * Math.pow(1.8, plot.level));
  if (playerProfile.cash < upgradeCost) {
    return res.status(400).json({ error: `Saldo insuficiente para upgrade. Custo: $${upgradeCost}` });
  }

  playerProfile.cash -= upgradeCost;
  plot.level += 1;

  res.json({ success: true, message: `${plot.name} aprimorado para Nível ${plot.level}! Throughput aumentado.`, player: playerProfile });
});

// Era Progression Advance
app.post('/api/player/advance-era', (req, res) => {
  const nextEraId = playerProfile.eraId + 1;
  const nextEra = ERAS.find(e => e.id === nextEraId);
  if (!nextEra) return res.status(400).json({ error: 'Você já atingiu a Era Máxima (Corporação Espacial).' });

  // Calculate Net Worth
  let inventoryValue = 0;
  Object.entries(playerProfile.warehouse.inventory).forEach(([itemId, qty]) => {
    const pr = gameState.marketPrices[itemId]?.price || 10;
    inventoryValue += qty * pr;
  });
  const currentNetWorth = playerProfile.cash + inventoryValue;

  if (currentNetWorth < nextEra.targetNetWorth) {
    return res.status(400).json({
      error: `Patrimônio Líquido insuficiente para a Era ${nextEra.name}. Necessário: $${nextEra.targetNetWorth.toLocaleString()}, Atual: $${Math.floor(currentNetWorth).toLocaleString()}`
    });
  }

  playerProfile.eraId = nextEraId;
  playerProfile.powerGrid.total += 100 * nextEraId;
  playerProfile.warehouse.capacity += 5000 * nextEraId;

  broadcast({
    type: 'NEW_CHAT',
    message: {
      id: Date.now(),
      user: 'SISTEMA_GLOBAL',
      text: `🎉 A corporação @${playerProfile.name} acaba de avançar para a ${nextEra.name.toUpperCase()}! Mercado em festa!`,
      time: new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })
    }
  });

  res.json({ success: true, message: `Parabéns! Você alcançou a ${nextEra.name}! Novos equipamentos e contratos espaciais desbloqueados!`, player: playerProfile });
});

// ----------------------------------------------------------------------------
// 5. DYNAMIC AI INVENTION ENGINE (P&D / Lab de Criação Livre)
// ----------------------------------------------------------------------------
app.post('/api/research/invent', async (req, res) => {
  const { input1, input2, catalyst } = req.body;
  if (!input1 || !input2) {
    return res.status(400).json({ error: 'Selecione pelo menos 2 insumos ou componentes para a síntese de P&D.' });
  }

  const costResearch = 3500;
  if (playerProfile.cash < costResearch) {
    return res.status(400).json({ error: `Saldo insuficiente para bancada de P&D. Taxa de prototipagem: $${costResearch}` });
  }

  playerProfile.cash -= costResearch;

  // Consume 1 unit of each from inventory if available
  if ((playerProfile.warehouse.inventory[input1] || 0) >= 1) {
    playerProfile.warehouse.inventory[input1] -= 1;
  }
  if ((playerProfile.warehouse.inventory[input2] || 0) >= 1) {
    playerProfile.warehouse.inventory[input2] -= 1;
  }

  // Check if we can use Gemini API
  let aiResult = null;
  if (GEMINI_API_KEY) {
    try {
      const prompt = `Você é o motor de IA do jogo Magnata Tycoon Online. 
O jogador fundiu na bancada de inovação os seguintes insumos/componentes industriais:
Insumo 1: "${input1}"
Insumo 2: "${input2}"
Catalisador opcional: "${catalyst || 'Nenhum'}"

Crie um novo produto ou maquinário industrial único, coerente com uma evolução de engenharia industrial, automação ou tecnologia espacial.
Responda EXCLUSIVAMENTE em formato JSON com o seguinte schema exato:
{
  "name": "Nome da Invenção",
  "category": "Produto" ou "Maquinário",
  "tier": 1 a 5,
  "icon": "emoji temático único",
  "lore": "Breve descrição técnica inovadora e lore (máximo 2 linhas)",
  "energyEfficiency": número de 75 a 99,
  "marketValue": número estimado do valor de mercado (entre 800 e 85000),
  "powerConsumption": número em MW (de 5 a 120),
  "outputRatePerHour": número de itens produzidos por hora (entre 10 e 150)
}`;

      const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${GEMINI_API_KEY}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          contents: [{ parts: [{ text: prompt }] }],
          generationConfig: { responseMimeType: 'application/json' }
        })
      });

      if (response.ok) {
        const jsonResp = await response.json();
        const textContent = jsonResp.candidates?.[0]?.content?.parts?.[0]?.text;
        if (textContent) {
          aiResult = JSON.parse(textContent);
        }
      }
    } catch (err) {
      console.warn('Gemini API call skipped or timed out, using procedural neural generator:', err.message);
    }
  }

  // Fallback Procedural Neural Generation
  if (!aiResult) {
    const PREFIXES = ['Nano', 'Termo', 'Bio', 'Eletro', 'Quântico', 'Ciber', 'Hiper', 'Sintético', 'Plasma'];
    const SUFFIXES = ['Refinador', 'Sintetizador', 'Transdutor', 'Catalisador', 'Reator', 'Extrusor', 'Indutor', 'Condensador'];
    const ICONS = ['⚡', '🧬', '🔬', '💎', '🚀', '🔮', '🦾', '⚛️', '🪐'];

    const pfx = PREFIXES[Math.floor(Math.random() * PREFIXES.length)];
    const sfx = SUFFIXES[Math.floor(Math.random() * SUFFIXES.length)];
    const chosenIcon = ICONS[Math.floor(Math.random() * ICONS.length)];
    const tier = Math.min(5, Math.max(1, playerProfile.eraId + (Math.random() > 0.5 ? 1 : 0)));

    aiResult = {
      name: `${pfx}-${sfx} Mark ${Math.floor(Math.random() * 9 + 1)}`,
      category: Math.random() > 0.4 ? 'Maquinário' : 'Produto',
      tier,
      icon: chosenIcon,
      lore: `Fusão molecular de ${input1} com matriz de ${input2}, gerando estabilidade atômica para produção contínua de alta escala.`,
      energyEfficiency: Math.floor(82 + Math.random() * 16),
      marketValue: Math.floor(2500 * Math.pow(2.2, tier)),
      powerConsumption: Math.floor(15 * tier),
      outputRatePerHour: Math.floor(25 * tier)
    };
  }

  // Register Invention in game
  const newInventedItem = {
    id: 'inv_' + Date.now(),
    name: aiResult.name,
    category: aiResult.category,
    tier: aiResult.tier,
    icon: aiResult.icon || '🔬',
    lore: aiResult.lore,
    cost: Math.floor(aiResult.marketValue * 1.6),
    basePrice: aiResult.marketValue,
    power: aiResult.powerConsumption || 25,
    outputRate: Math.max(2, Math.floor((aiResult.outputRatePerHour || 30) / 10)),
    inputId: input1,
    outputId: 'inv_prod_' + Date.now(),
    energyEfficiency: aiResult.energyEfficiency || 88,
    inventor: playerProfile.name
  };

  gameState.inventedItems.unshift(newInventedItem);
  playerProfile.patents.push(newInventedItem);

  // If it can also be traded as a commodity
  gameState.marketPrices[newInventedItem.id] = {
    price: newInventedItem.basePrice,
    change24h: '+12.4',
    volume: 12000,
    history: [newInventedItem.basePrice * 0.9, newInventedItem.basePrice]
  };
  gameState.orderBook[newInventedItem.id] = {
    bids: [{ id: 'b_inv_1', price: Math.floor(newInventedItem.basePrice * 0.92), amount: 15, user: 'InvestidorAnjo' }],
    asks: [{ id: 'a_inv_1', price: Math.floor(newInventedItem.basePrice * 1.08), amount: 10, user: 'PatenteExpress' }],
    history: []
  };

  broadcast({
    type: 'NEW_INVENTION',
    item: newInventedItem
  });

  res.json({
    success: true,
    message: `Patente registrada com sucesso! Nova tecnologia: ${newInventedItem.name}`,
    invention: newInventedItem,
    player: playerProfile
  });
});

// Buy Transport Vehicle
app.post('/api/logistics/buy-vehicle', (req, res) => {
  const { vehicleType } = req.body;
  const VEHICLE_TYPES = {
    van: { name: 'Caminhonete Rural 4x4', cost: 6500, cap: 300, speed: 'Rápido' },
    truck: { name: 'Caminhão Baú Rodoviário', cost: 32000, cap: 1500, speed: 'Médio' },
    ship: { name: 'Navio Porta-Contêineres', cost: 240000, cap: 12000, speed: 'Lento' },
    orbital: { name: 'Cargueiro Orbital Sub-Luz', cost: 1800000, cap: 60000, speed: 'Hiper-Rápido' }
  };

  const v = VEHICLE_TYPES[vehicleType];
  if (!v) return res.status(400).json({ error: 'Tipo de veículo não reconhecido.' });

  if (playerProfile.cash < v.cost) {
    return res.status(400).json({ error: `Saldo insuficiente. Custo: $${v.cost.toLocaleString()}` });
  }

  playerProfile.cash -= v.cost;
  const existing = playerProfile.fleet.find(f => f.name === v.name);
  if (existing) {
    existing.count += 1;
  } else {
    playerProfile.fleet.push({
      id: 'v_' + Date.now(),
      name: v.name,
      capacity: v.cap,
      speed: v.speed,
      status: 'LIVRE',
      count: 1
    });
  }

  res.json({ success: true, message: `Aquisição de frota concluída: +1 ${v.name}!`, player: playerProfile });
});

// Fallback HTML router
app.use((req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// ----------------------------------------------------------------------------
// 6. SERVER BOOTSTRAP
// ----------------------------------------------------------------------------
server.listen(PORT, '0.0.0.0', () => {
  console.log(`=======================================================`);
  console.log(`🚀 MAGNATA TYCOON ONLINE - ENGINE INICIADA COM SUCESSO`);
  console.log(`📡 Servidor Node.js escutando em http://0.0.0.0:${PORT}`);
  console.log(`⚡ WebSocket Server ativo para multiplayer massivo P2P`);
  console.log(`🤖 Motor IA P&D: ${GEMINI_API_KEY ? 'Gemini 2.5 Conectado' : 'Gerador Neural Procedural Ativo'}`);
  console.log(`=======================================================`);
});
