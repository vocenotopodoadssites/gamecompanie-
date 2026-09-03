// ============================================================================
// MAGNATA TYCOON ONLINE - CLIENT CONTROLLER & SIMULATION (WebSockets + Audio)
// ============================================================================

(function () {
  'use strict';

  // State Containers
  let gameState = null;
  let currentSelectedCommodityId = 'trigo';
  let orderSide = 'BUY'; // 'BUY' or 'SELL'
  let activePlotIdForModal = null;
  let socket = null;
  let soundEnabled = true;

  // Web Audio Synthesizer
  let audioCtx = null;
  function getAudioCtx() {
    if (!audioCtx) {
      const AudioContext = window.AudioContext || window.webkitAudioContext;
      if (AudioContext) audioCtx = new AudioContext();
    }
    if (audioCtx && audioCtx.state === 'suspended') {
      audioCtx.resume();
    }
    return audioCtx;
  }

  function playSound(type) {
    if (!soundEnabled) return;
    try {
      const ctx = getAudioCtx();
      if (!ctx) return;
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);

      const now = ctx.currentTime;

      if (type === 'coin') {
        // High dual chime
        osc.type = 'sine';
        osc.frequency.setValueAtTime(987.77, now); // B5
        osc.frequency.setValueAtTime(1318.51, now + 0.08); // E6
        gain.gain.setValueAtTime(0.15, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.35);
        osc.start(now);
        osc.stop(now + 0.35);
      } else if (type === 'sparkle') {
        // Sci-fi sweep
        osc.type = 'triangle';
        osc.frequency.setValueAtTime(440, now);
        osc.frequency.exponentialRampToValueAtTime(1760, now + 0.25);
        gain.gain.setValueAtTime(0.12, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.3);
        osc.start(now);
        osc.stop(now + 0.3);
      } else if (type === 'upgrade') {
        // Industrial horn / power hum
        osc.type = 'sawtooth';
        osc.frequency.setValueAtTime(160, now);
        osc.frequency.exponentialRampToValueAtTime(420, now + 0.2);
        gain.gain.setValueAtTime(0.1, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.4);
        osc.start(now);
        osc.stop(now + 0.4);
      } else if (type === 'click') {
        osc.type = 'sine';
        osc.frequency.setValueAtTime(800, now);
        gain.gain.setValueAtTime(0.05, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.05);
        osc.start(now);
        osc.stop(now + 0.05);
      }
    } catch (e) {
      // Audio context might be restricted before user gesture
    }
  }

  // Toast Notification System
  function showToast(msg, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;
    const toast = document.createElement('div');
    toast.className = `toast-msg ${type}`;
    toast.textContent = msg;
    container.appendChild(toast);
    setTimeout(() => {
      toast.style.opacity = '0';
      setTimeout(() => toast.remove(), 300);
    }, 4000);
  }

  // Formatting helpers
  function fmtCurrency(val) {
    return '$' + Number(val || 0).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  // --------------------------------------------------------------------------
  // WEBSOCKET SETUP & SYNC
  // --------------------------------------------------------------------------
  function initWebSocket() {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${protocol}//${window.location.host}`;
    
    socket = new WebSocket(wsUrl);

    socket.onopen = () => {
      console.log('Conectado ao WebSocket do Magnata Tycoon.');
    };

    socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        handleServerMessage(data);
      } catch (e) {
        console.error('Erro ao processar pacote WS:', e);
      }
    };

    socket.onclose = () => {
      console.warn('Conexão WS encerrada. Reconectando em 3 segundos...');
      setTimeout(initWebSocket, 3000);
    };

    socket.onerror = (err) => {
      console.error('Erro de WebSocket:', err);
    };
  }

  function handleServerMessage(msg) {
    if (msg.type === 'INIT') {
      gameState = msg.state;
      renderAll();
    } else if (msg.type === 'TICK') {
      if (!gameState) return;
      gameState.serverTick = msg.tick;
      gameState.marketPrices = msg.marketPrices;
      gameState.economicEvent = msg.economicEvent;
      
      // Update player profile fields
      if (msg.playerSync && gameState.player) {
        gameState.player.cash = msg.playerSync.cash;
        gameState.player.warehouse.inventory = msg.playerSync.inventory;
        gameState.player.warehouse.used = msg.playerSync.warehouseUsed;
        gameState.player.powerGrid.used = msg.playerSync.powerUsed;
        gameState.player.factoryPlots = msg.playerSync.plots;
      }

      updateHeaderStats();
      renderFactoryPlots();
      renderMarketChart();
      renderOrderBook();
      renderOverviewInventory();
      renderWarehouseTable();
    } else if (msg.type === 'EVENT_ALERT') {
      gameState.economicEvent = msg.event;
      showToast(`📢 EVENTO GLOBAL: ${msg.event.title}!`, 'info');
      updateHeaderStats();
    } else if (msg.type === 'NEW_CHAT') {
      if (gameState && gameState.chatMessages) {
        gameState.chatMessages.push(msg.message);
        renderChatMessages();
      }
    } else if (msg.type === 'NEW_CONTRACT') {
      if (gameState && gameState.activeContracts) {
        gameState.activeContracts.unshift(msg.contract);
        renderContracts();
      }
    } else if (msg.type === 'CONTRACT_UPDATED') {
      if (gameState && gameState.activeContracts) {
        const c = gameState.activeContracts.find(x => x.id === msg.contract.id);
        if (c) c.status = msg.contract.status;
        renderContracts();
      }
    } else if (msg.type === 'NEW_INVENTION') {
      if (gameState && gameState.inventedItems) {
        gameState.inventedItems.unshift(msg.item);
        renderPatentsList();
      }
    } else if (msg.type === 'ORDER_BOOK_UPDATE') {
      if (gameState && gameState.orderBook) {
        gameState.orderBook[msg.itemId] = msg.orderBook;
        if (currentSelectedCommodityId === msg.itemId) {
          renderOrderBook();
        }
      }
    }
  }

  // --------------------------------------------------------------------------
  // MAIN RENDER ENGINE
  // --------------------------------------------------------------------------
  function renderAll() {
    if (!gameState) return;
    updateHeaderStats();
    renderErasTimeline();
    renderFactoryPlots();
    populateLabSelects();
    populateMarketSelect();
    renderMarketChart();
    renderOrderBook();
    renderChatMessages();
    renderContracts();
    renderWarehouseTable();
    renderFleetCards();
    renderLeaderboard();
    renderPatentsList();
    renderOverviewInventory();
  }

  function updateHeaderStats() {
    if (!gameState || !gameState.player) return;
    const p = gameState.player;
    const currentEra = gameState.eras.find(e => e.id === p.eraId) || gameState.eras[0];

    // Header Quick Stats
    document.getElementById('playerCash').textContent = fmtCurrency(p.cash);
    
    let invValue = 0;
    Object.entries(p.warehouse.inventory || {}).forEach(([itemId, qty]) => {
      const pr = gameState.marketPrices[itemId]?.price || 10;
      invValue += qty * pr;
    });
    const netWorth = p.cash + invValue;
    document.getElementById('playerNetWorth').textContent = fmtCurrency(netWorth);
    document.getElementById('playerEraBadge').textContent = `${currentEra.icon} ${currentEra.name}`;

    // Macro Event
    if (gameState.economicEvent) {
      document.getElementById('eventTitle').textContent = `${gameState.economicEvent.title} — ${gameState.economicEvent.desc}`;
      document.getElementById('eventTimer').textContent = `${gameState.economicEvent.remainingSeconds}s`;
    }

    // KPI Badges
    document.getElementById('kpiPower').textContent = `${p.powerGrid.used} / ${p.powerGrid.total} MW`;
    document.getElementById('kpiWarehouse').textContent = `${p.warehouse.used} / ${p.warehouse.capacity} m³`;
    
    let totalVehicles = 0;
    p.fleet.forEach(v => totalVehicles += v.count);
    document.getElementById('kpiFleet').textContent = `${totalVehicles} Veículos`;
    document.getElementById('kpiPatents').textContent = `${p.patents?.length || 0} Registradas`;
  }

  // 1. ERAS TIMELINE
  function renderErasTimeline() {
    const container = document.getElementById('erasStepper');
    if (!container || !gameState) return;
    container.innerHTML = '';

    const currentEraId = gameState.player.eraId;
    document.getElementById('currentEraStep').textContent = `Estágio ${currentEraId} de ${gameState.eras.length}`;

    gameState.eras.forEach(era => {
      const item = document.createElement('div');
      item.className = 'era-step-item';
      if (era.id < currentEraId) item.classList.add('completed');
      if (era.id === currentEraId) item.classList.add('active');

      item.innerHTML = `
        <div class="era-step-icon">${era.icon}</div>
        <div>
          <span class="era-step-title">${era.name}</span>
          <span class="era-step-desc">${era.id < currentEraId ? 'Concluída ✓' : (era.id === currentEraId ? 'Em andamento' : 'Bloqueada')}</span>
        </div>
      `;
      container.appendChild(item);
    });

    // Advance Era Card
    const nextEra = gameState.eras.find(e => e.id === currentEraId + 1);
    const advanceBtn = document.getElementById('btnAdvanceEra');
    if (nextEra) {
      document.getElementById('nextEraTitle').textContent = `Próxima Era: ${nextEra.name}`;
      document.getElementById('nextEraRequirement').textContent = `Requer Patrimônio Líquido de ${fmtCurrency(nextEra.targetNetWorth)}`;
      
      let invValue = 0;
      Object.entries(gameState.player.warehouse.inventory || {}).forEach(([itemId, qty]) => {
        const pr = gameState.marketPrices[itemId]?.price || 10;
        invValue += qty * pr;
      });
      const netWorth = gameState.player.cash + invValue;
      const pct = Math.min(100, Math.floor((netWorth / nextEra.targetNetWorth) * 100));
      document.getElementById('eraProgressFill').style.width = pct + '%';

      advanceBtn.disabled = netWorth < nextEra.targetNetWorth;
      advanceBtn.textContent = netWorth >= nextEra.targetNetWorth ? `Evoluir para ${nextEra.name} 🚀` : `Meta: ${pct}% Concluída`;
    } else {
      document.getElementById('nextEraTitle').textContent = `Parabéns! Você alcançou o ápice corporativo espacial.`;
      document.getElementById('nextEraRequirement').textContent = `Mineração orbital e expansão interestelar ativas.`;
      document.getElementById('eraProgressFill').style.width = '100%';
      advanceBtn.disabled = true;
      advanceBtn.textContent = `Era Máxima Atingida`;
    }
  }

  // 2. FACTORY PLOTS
  function renderFactoryPlots() {
    const container = document.getElementById('factoryPlotsGrid');
    if (!container || !gameState) return;
    container.innerHTML = '';

    const plots = gameState.player.factoryPlots || [];
    plots.forEach(plot => {
      const card = document.createElement('div');
      card.className = 'plot-card';

      if (!plot.machineId) {
        card.classList.add('empty');
        card.innerHTML = `
          <div style="font-size: 2rem; margin-bottom: 8px;">➕</div>
          <h3 style="font-family: var(--font-display);">Lote ${plot.plotId + 1} Vazio</h3>
          <span style="font-size: 0.8rem; color: var(--text-muted);">Clique para instalar maquinário</span>
        `;
        card.onclick = () => openInstallMachineModal(plot.plotId);
      } else {
        const machine = gameState.machines.find(m => m.id === plot.machineId) || gameState.inventedItems?.find(i => i.id === plot.machineId);
        const icon = machine ? machine.icon : '⚙️';
        const progressPct = Math.floor((plot.progress || 0) * 100);

        card.innerHTML = `
          <div class="plot-header">
            <span class="plot-title">${icon} ${plot.name}</span>
            <span class="badge">Nível ${plot.level}</span>
          </div>
          <div class="plot-stats-row">
            <div>Consumo: <strong>${(machine?.power || 0) * plot.level} MW</strong></div>
            <div>Produção: <strong>+${(machine?.outputRate || 2) * plot.level} / ciclo</strong></div>
            <div>Entrada: <strong>${machine?.inputId ? getCommodityName(machine.inputId) : 'Autônoma'}</strong></div>
            <div>Saída: <strong>${getCommodityName(machine?.outputId)}</strong></div>
          </div>
          <div class="plot-progress-bar">
            <div class="plot-progress-fill" style="width: ${progressPct}%"></div>
          </div>
          <div class="plot-actions">
            <button class="btn btn-outline btn-sm btn-block" onclick="upgradePlot(${plot.plotId})">
              Upgrade (Nível ${plot.level + 1})
            </button>
          </div>
        `;
      }
      container.appendChild(card);
    });

    document.getElementById('factoryPowerChip').textContent = `⚡ Demanda: ${gameState.player.powerGrid.used} / ${gameState.player.powerGrid.total} MW`;
  }

  function getCommodityName(id) {
    if (!id) return 'Nenhum';
    const c = gameState?.commodities.find(x => x.id === id);
    if (c) return `${c.icon} ${c.name}`;
    const inv = gameState?.inventedItems?.find(x => x.id === id);
    if (inv) return `${inv.icon} ${inv.name}`;
    return id;
  }

  // 3. LAB & AI WORKBENCH
  function populateLabSelects() {
    const sel1 = document.getElementById('selectInput1');
    const sel2 = document.getElementById('selectInput2');
    const selMarket = document.getElementById('marketItemSelect');
    const selContract = document.getElementById('contractItemSelect');

    if (!sel1 || !sel2 || !gameState) return;

    const allItems = [...gameState.commodities, ...(gameState.inventedItems || [])];

    [sel1, sel2, selMarket, selContract].forEach(sel => {
      if (!sel) return;
      const curVal = sel.value;
      sel.innerHTML = '';
      allItems.forEach(item => {
        const opt = document.createElement('option');
        opt.value = item.id;
        opt.textContent = `${item.icon || '📦'} ${item.name} (${item.category})`;
        sel.appendChild(opt);
      });
      if (curVal) sel.value = curVal;
    });

    if (!sel2.value && allItems.length > 1) sel2.selectedIndex = 1;
    updateLabPreviews();
  }

  function updateLabPreviews() {
    const sel1 = document.getElementById('selectInput1');
    const sel2 = document.getElementById('selectInput2');
    const selCat = document.getElementById('selectCatalyst');

    if (sel1 && document.getElementById('previewInput1')) {
      document.getElementById('previewInput1').textContent = sel1.options[sel1.selectedIndex]?.text || '';
    }
    if (sel2 && document.getElementById('previewInput2')) {
      document.getElementById('previewInput2').textContent = sel2.options[sel2.selectedIndex]?.text || '';
    }
    if (selCat && document.getElementById('previewCatalyst')) {
      document.getElementById('previewCatalyst').textContent = selCat.value;
    }
  }

  async function synthesizeAI() {
    const input1 = document.getElementById('selectInput1').value;
    const input2 = document.getElementById('selectInput2').value;
    const catalyst = document.getElementById('selectCatalyst').value;

    const btn = document.getElementById('btnSynthesizeAI');
    btn.disabled = true;
    btn.innerHTML = '⏳ Processando Matriz Neural...';
    playSound('sparkle');

    try {
      const resp = await fetch('/api/research/invent', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ input1, input2, catalyst })
      });
      const data = await resp.json();

      if (!resp.ok) {
        showToast(data.error || 'Falha na sintetização de P&D.', 'error');
      } else {
        showToast(`✨ ${data.message}`, 'success');
        playSound('upgrade');
        gameState.player = data.player;
        displayBlueprintResult(data.invention);
        renderAll();
      }
    } catch (e) {
      showToast('Erro de comunicação com o servidor de IA.', 'error');
    } finally {
      btn.disabled = false;
      btn.innerHTML = '<span class="sparkle-icon">✨</span> Sintetizar Inovação com IA';
    }
  }

  function displayBlueprintResult(inv) {
    const card = document.getElementById('blueprintResultCard');
    const catTag = document.getElementById('bpCategory');
    const body = document.getElementById('blueprintBody');

    catTag.textContent = `PATENTE TIER ${inv.tier} • ${inv.category.toUpperCase()}`;
    body.innerHTML = `
      <div class="blueprint-active-view">
        <div class="bp-item-header">
          <div class="bp-icon-large">${inv.icon}</div>
          <div>
            <div class="bp-name">${inv.name}</div>
            <div class="bp-tier">Grau de Inovação: Tier ${inv.tier} ★★★</div>
          </div>
        </div>
        <div class="bp-lore">${inv.lore}</div>
        <div class="bp-specs-grid">
          <div>Eficiência Energética: <span class="bp-spec-val">${inv.energyEfficiency}%</span></div>
          <div>Valor de Referência: <span class="bp-spec-val">${fmtCurrency(inv.basePrice)}</span></div>
          <div>Consumo / Saída: <span class="bp-spec-val">${inv.power} MW / +${inv.outputRate} un.</span></div>
          <div>Inventor: <span class="bp-spec-val">${inv.inventor}</span></div>
        </div>
      </div>
    `;
  }

  function renderPatentsList() {
    const list = document.getElementById('playerPatentsList');
    const badge = document.getElementById('patentsCountBadge');
    if (!list || !gameState) return;

    const patents = gameState.player.patents || [];
    badge.textContent = `${patents.length} Patentes`;

    if (patents.length === 0) {
      list.innerHTML = `<p class="text-muted empty-state">Nenhuma patente criada ainda. Use a bancada de P&D para inventar sua primeira tecnologia!</p>`;
      return;
    }

    list.innerHTML = '';
    patents.forEach(pat => {
      const item = document.createElement('div');
      item.className = 'patent-card-item';
      item.innerHTML = `
        <div style="display: flex; align-items: center; justify-content: space-between;">
          <strong>${pat.icon} ${pat.name}</strong>
          <span class="badge" style="background: rgba(139, 92, 246, 0.2); color: #c084fc;">Tier ${pat.tier}</span>
        </div>
        <div style="font-size: 0.78rem; color: var(--text-muted);">${pat.lore}</div>
        <div style="font-size: 0.8rem; font-family: var(--font-mono); color: var(--accent-green);">${fmtCurrency(pat.basePrice)}</div>
      `;
      list.appendChild(item);
    });
  }

  // 4. ORDER BOOK & P2P MARKET
  function populateMarketSelect() {
    const sel = document.getElementById('marketItemSelect');
    if (!sel || !gameState) return;

    const cur = sel.value || currentSelectedCommodityId;
    sel.innerHTML = '';
    gameState.commodities.forEach(c => {
      const opt = document.createElement('option');
      opt.value = c.id;
      opt.textContent = `${c.icon} ${c.name}`;
      sel.appendChild(opt);
    });

    // Also include player invented items
    (gameState.inventedItems || []).forEach(inv => {
      const opt = document.createElement('option');
      opt.value = inv.id;
      opt.textContent = `${inv.icon} ${inv.name} (Inovação)`;
      sel.appendChild(opt);
    });

    sel.value = cur;
    currentSelectedCommodityId = sel.value;
  }

  function renderMarketChart() {
    const canvas = document.getElementById('marketCanvasChart');
    if (!canvas || !gameState) return;
    const ctx = canvas.getContext('2d');
    const mp = gameState.marketPrices[currentSelectedCommodityId];
    if (!mp) return;

    // Header info
    document.getElementById('marketCurPrice').textContent = fmtCurrency(mp.price);
    const changeEl = document.getElementById('marketCurChange');
    const changeNum = parseFloat(mp.change24h);
    changeEl.textContent = (changeNum >= 0 ? '+' : '') + mp.change24h + '%';
    changeEl.className = 'price-change ' + (changeNum >= 0 ? 'positive' : 'negative');

    // Update form input price default if not edited
    const priceInput = document.getElementById('orderInputPrice');
    if (priceInput && (!priceInput.value || priceInput.dataset.item !== currentSelectedCommodityId)) {
      priceInput.value = mp.price;
      priceInput.dataset.item = currentSelectedCommodityId;
      updateOrderTotal();
    }

    // Canvas drawing
    const w = canvas.width;
    const h = canvas.height;
    ctx.clearRect(0, 0, w, h);

    const history = mp.history || [mp.price];
    if (history.length < 2) return;

    const minP = Math.min(...history) * 0.98;
    const maxP = Math.max(...history) * 1.02;
    const range = maxP - minP || 1;

    // Grid lines
    ctx.strokeStyle = '#1e293b';
    ctx.lineWidth = 1;
    for (let i = 1; i <= 3; i++) {
      const y = (h / 4) * i;
      ctx.beginPath();
      ctx.moveTo(0, y);
      ctx.lineTo(w, y);
      ctx.stroke();
    }

    // Chart Gradient Area
    const grad = ctx.createLinearGradient(0, 0, 0, h);
    grad.addColorStop(0, 'rgba(6, 182, 212, 0.25)');
    grad.addColorStop(1, 'rgba(6, 182, 212, 0.0)');

    ctx.beginPath();
    history.forEach((val, idx) => {
      const x = (idx / (history.length - 1)) * w;
      const y = h - ((val - minP) / range) * (h - 20) - 10;
      if (idx === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    });

    ctx.lineTo(w, h);
    ctx.lineTo(0, h);
    ctx.closePath();
    ctx.fillStyle = grad;
    ctx.fill();

    // Chart Stroke Line
    ctx.beginPath();
    history.forEach((val, idx) => {
      const x = (idx / (history.length - 1)) * w;
      const y = h - ((val - minP) / range) * (h - 20) - 10;
      if (idx === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    });
    ctx.strokeStyle = '#06b6d4';
    ctx.lineWidth = 2.5;
    ctx.stroke();
  }

  function renderOrderBook() {
    const ob = gameState?.orderBook[currentSelectedCommodityId];
    if (!ob) return;

    const asksContainer = document.getElementById('obAsksList');
    const bidsContainer = document.getElementById('obBidsList');
    const spreadBar = document.getElementById('obSpreadBar');
    const historyContainer = document.getElementById('obHistoryList');

    // Asks (Sell)
    asksContainer.innerHTML = '';
    const maxAskVol = Math.max(...(ob.asks.map(a => a.amount)), 100);
    ob.asks.slice(0, 6).forEach(ask => {
      const row = document.createElement('div');
      row.className = 'ob-row';
      const depthPct = Math.floor((ask.amount / maxAskVol) * 100);
      row.innerHTML = `
        <div class="depth-fill" style="width: ${depthPct}%"></div>
        <span>$${ask.price.toFixed(1)}</span>
        <span>${ask.amount} un.</span>
        <span style="color: var(--text-muted);">${ask.user}</span>
      `;
      row.onclick = () => {
        document.getElementById('orderInputPrice').value = ask.price;
        updateOrderTotal();
      };
      asksContainer.appendChild(row);
    });

    // Spread
    const topAsk = ob.asks[0]?.price || 0;
    const topBid = ob.bids[0]?.price || 0;
    const spread = topAsk && topBid ? Math.abs(topAsk - topBid).toFixed(2) : '0.00';
    spreadBar.innerHTML = `<span>SPREAD: $${spread} (${((spread / (topAsk || 1)) * 100).toFixed(1)}%)</span>`;

    // Bids (Buy)
    bidsContainer.innerHTML = '';
    const maxBidVol = Math.max(...(ob.bids.map(b => b.amount)), 100);
    ob.bids.slice(0, 6).forEach(bid => {
      const row = document.createElement('div');
      row.className = 'ob-row';
      const depthPct = Math.floor((bid.amount / maxBidVol) * 100);
      row.innerHTML = `
        <div class="depth-fill" style="width: ${depthPct}%"></div>
        <span>$${bid.price.toFixed(1)}</span>
        <span>${bid.amount} un.</span>
        <span style="color: var(--text-muted);">${bid.user}</span>
      `;
      row.onclick = () => {
        document.getElementById('orderInputPrice').value = bid.price;
        updateOrderTotal();
      };
      bidsContainer.appendChild(row);
    });

    // History
    historyContainer.innerHTML = '';
    (ob.history || []).slice(0, 6).forEach(tx => {
      const hRow = document.createElement('div');
      hRow.className = 'trade-hist-row';
      hRow.innerHTML = `
        <span style="color: var(--accent-cyan);">$${tx.price}</span>
        <span>${tx.amount} un.</span>
        <span style="color: var(--text-muted);">${tx.buyer} ➔ ${tx.seller}</span>
        <span style="color: var(--text-muted); font-size: 0.7rem;">${tx.time}</span>
      `;
      historyContainer.appendChild(hRow);
    });
  }

  function updateOrderTotal() {
    const p = parseFloat(document.getElementById('orderInputPrice').value) || 0;
    const q = parseInt(document.getElementById('orderInputQty').value, 10) || 0;
    document.getElementById('orderTotalEstimado').textContent = fmtCurrency(p * q);
  }

  async function submitOrder() {
    const price = parseFloat(document.getElementById('orderInputPrice').value);
    const amount = parseInt(document.getElementById('orderInputQty').value, 10);

    if (!price || !amount) {
      showToast('Preencha preço e quantidade válidos.', 'error');
      return;
    }

    try {
      const resp = await fetch('/api/order/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          itemId: currentSelectedCommodityId,
          side: orderSide,
          price,
          amount
        })
      });

      const data = await resp.json();
      if (!resp.ok) {
        showToast(data.error || 'Falha ao processar ordem.', 'error');
      } else {
        playSound('coin');
        showToast(data.message, 'success');
        gameState.player = data.player;
        updateHeaderStats();
        renderWarehouseTable();
      }
    } catch (e) {
      showToast('Erro de rede ao enviar ordem.', 'error');
    }
  }

  // 5. CHAT & CONTRACTS
  function renderChatMessages() {
    const feed = document.getElementById('chatFeed');
    if (!feed || !gameState) return;
    feed.innerHTML = '';

    (gameState.chatMessages || []).forEach(msg => {
      const b = document.createElement('div');
      b.className = 'chat-bubble';
      if (msg.user.includes('Você')) b.classList.add('own');
      b.innerHTML = `
        <div class="chat-meta">
          <span>@${msg.user}</span>
          <span class="chat-time">${msg.time}</span>
        </div>
        <div class="chat-text">${msg.text}</div>
      `;
      feed.appendChild(b);
    });
    feed.scrollTop = feed.scrollHeight;
  }

  function sendChatMessage() {
    const input = document.getElementById('chatInputText');
    const text = input.value.trim();
    if (!text || !socket) return;
    socket.send(JSON.stringify({ type: 'CHAT_MESSAGE', text }));
    input.value = '';
    playSound('click');
  }

  function renderContracts() {
    const list = document.getElementById('contractsList');
    if (!list || !gameState) return;
    list.innerHTML = '';

    (gameState.activeContracts || []).forEach(c => {
      const item = document.createElement('div');
      item.className = 'contract-card-item';
      const isOpen = c.status === 'OPEN';

      item.innerHTML = `
        <div class="contract-header-row">
          <span>${c.id} • ${c.from}</span>
          <span class="contract-status ${isOpen ? 'open' : 'completed'}">${c.status}</span>
        </div>
        <div style="font-size: 0.85rem; color: var(--text-muted);">
          Destinatário: <strong>${c.to}</strong> | Fornecimento: <strong>${c.amount} un. de ${getCommodityName(c.item)}</strong>
        </div>
        <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 6px;">
          <div style="font-family: var(--font-mono); font-weight: 700; color: var(--accent-green);">
            ${fmtCurrency(c.total)} ($${c.pricePerUnit}/un.)
          </div>
          ${isOpen ? `<button class="btn btn-sm btn-primary" onclick="acceptContract('${c.id}')">Liquidar Contrato 🤝</button>` : '<span style="font-size: 0.78rem; color: var(--accent-green);">Liquidado ✓</span>'}
        </div>
      `;
      list.appendChild(item);
    });
  }

  window.acceptContract = async function (id) {
    try {
      const resp = await fetch('/api/contract/accept', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ contractId: id })
      });
      const data = await resp.json();
      if (!resp.ok) {
        showToast(data.error || 'Erro ao aceitar contrato.', 'error');
      } else {
        playSound('coin');
        showToast(data.message, 'success');
        gameState.player = data.player;
        updateHeaderStats();
        renderWarehouseTable();
      }
    } catch (e) {
      showToast('Falha na transação do contrato.', 'error');
    }
  };

  // 6. LOGISTICS & WAREHOUSE
  function renderWarehouseTable() {
    const tbody = document.getElementById('warehouseTableBody');
    if (!tbody || !gameState) return;
    tbody.innerHTML = '';

    const inv = gameState.player.warehouse.inventory || {};
    let totalCapUsed = 0;

    Object.entries(inv).forEach(([id, qty]) => {
      totalCapUsed += qty;
      const tr = document.createElement('tr');
      const curPrice = gameState.marketPrices[id]?.price || 10;
      const totalVal = qty * curPrice;

      tr.innerHTML = `
        <td><strong>${getCommodityName(id)}</strong></td>
        <td style="font-family: var(--font-mono);">${qty} un.</td>
        <td style="font-family: var(--font-mono); color: var(--accent-cyan);">${fmtCurrency(curPrice)}</td>
        <td style="font-family: var(--font-mono); color: var(--accent-green);">${fmtCurrency(totalVal)}</td>
        <td>
          <button class="btn btn-sm btn-outline" onclick="sellItemDirectly('${id}', ${qty})">Vender Tudo</button>
        </td>
      `;
      tbody.appendChild(tr);
    });

    const cap = gameState.player.warehouse.capacity || 2500;
    const pct = Math.min(100, Math.floor((totalCapUsed / cap) * 100));
    document.getElementById('warehouseCapText').textContent = `${totalCapUsed} / ${cap} m³ (${pct}%)`;
    document.getElementById('warehouseFillBar').style.width = pct + '%';
  }

  window.sellItemDirectly = function (id, qty) {
    currentSelectedCommodityId = id;
    document.getElementById('marketItemSelect').value = id;
    orderSide = 'SELL';
    document.getElementById('btnSideSell').click();
    document.getElementById('orderInputQty').value = qty;
    updateOrderTotal();

    // Switch to market tab
    document.querySelector('button[data-tab="tab-market"]').click();
  };

  function renderFleetCards() {
    const grid = document.getElementById('fleetCardsGrid');
    if (!grid || !gameState) return;
    grid.innerHTML = '';

    const FLEET_SHOP = [
      { key: 'van', name: 'Caminhonete Rural 4x4', icon: '🚙', cost: 6500, cap: '300 m³', speed: 'Rápido' },
      { key: 'truck', name: 'Caminhão Baú Rodoviário', icon: '🚛', cost: 32000, cap: '1.500 m³', speed: 'Médio' },
      { key: 'ship', name: 'Navio Porta-Contêineres', icon: '🚢', cost: 240000, cap: '12.000 m³', speed: 'Lento' },
      { key: 'orbital', name: 'Cargueiro Orbital Sub-Luz', icon: '🛸', cost: 1800000, cap: '60.000 m³', speed: 'Hiper-Rápido' }
    ];

    FLEET_SHOP.forEach(v => {
      const owned = gameState.player.fleet.find(f => f.name === v.name)?.count || 0;
      const card = document.createElement('div');
      card.className = 'fleet-card-item';
      card.innerHTML = `
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-size: 2rem;">${v.icon}</span>
          <span class="badge" style="color: var(--accent-cyan);">${owned} em posse</span>
        </div>
        <strong>${v.name}</strong>
        <div style="font-size: 0.8rem; color: var(--text-muted);">
          Capacidade: <strong>${v.cap}</strong> | Velocidade: <strong>${v.speed}</strong>
        </div>
        <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 8px;">
          <span style="font-family: var(--font-mono); color: var(--accent-amber);">${fmtCurrency(v.cost)}</span>
          <button class="btn btn-sm btn-primary" onclick="buyVehicle('${v.key}')">Comprar +1</button>
        </div>
      `;
      grid.appendChild(card);
    });
  }

  window.buyVehicle = async function (type) {
    try {
      const resp = await fetch('/api/logistics/buy-vehicle', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ vehicleType: type })
      });
      const data = await resp.json();
      if (!resp.ok) {
        showToast(data.error || 'Erro ao comprar veículo.', 'error');
      } else {
        playSound('coin');
        showToast(data.message, 'success');
        gameState.player = data.player;
        updateHeaderStats();
        renderFleetCards();
      }
    } catch (e) {
      showToast('Falha na comunicação de compra.', 'error');
    }
  };

  // 7. LEADERBOARD
  function renderLeaderboard() {
    const tbody = document.getElementById('leaderboardTableBody');
    if (!tbody || !gameState) return;
    tbody.innerHTML = '';

    (gameState.leaderboard || []).forEach(lead => {
      const tr = document.createElement('tr');
      const isPlayer = lead.name.includes('Você') || lead.name.includes('Sua Corporação');
      if (isPlayer) tr.style.background = 'rgba(6, 182, 212, 0.1)';

      tr.innerHTML = `
        <td style="font-family: var(--font-mono); font-weight: 700; color: ${lead.rank === 1 ? 'var(--accent-amber)' : 'var(--text-main)'}">#${lead.rank}</td>
        <td><strong>${lead.name}</strong> ${isPlayer ? '⭐' : ''}</td>
        <td>${lead.era}</td>
        <td><span class="badge" style="background: rgba(16, 185, 129, 0.15); color: var(--accent-green);">${lead.rating}</span></td>
        <td style="font-family: var(--font-mono); font-weight: 700; color: var(--accent-green);">${fmtCurrency(lead.netWorth)}</td>
      `;
      tbody.appendChild(tr);
    });
  }

  // OVERVIEW INVENTORY GRID
  function renderOverviewInventory() {
    const container = document.getElementById('overviewInventoryGrid');
    if (!container || !gameState) return;
    container.innerHTML = '';

    const inv = gameState.player.warehouse.inventory || {};
    const entries = Object.entries(inv);

    if (entries.length === 0) {
      container.innerHTML = '<span class="text-muted" style="font-size: 0.85rem;">Nenhum produto em estoque no momento.</span>';
      return;
    }

    entries.forEach(([id, qty]) => {
      const card = document.createElement('div');
      card.className = 'inv-tag-card';
      const c = gameState.commodities.find(x => x.id === id) || gameState.inventedItems?.find(x => x.id === id);
      const icon = c ? c.icon : '📦';
      const name = c ? c.name : id;
      card.innerHTML = `
        <div class="inv-tag-info">
          <span style="font-size: 1.2rem;">${icon}</span>
          <div>
            <div style="font-size: 0.85rem; font-weight: 600;">${name}</div>
            <div style="font-size: 0.72rem; color: var(--text-muted);">${c?.category || 'Manufaturado'}</div>
          </div>
        </div>
        <span class="inv-tag-val">${qty} un.</span>
      `;
      container.appendChild(card);
    });
  }

  // --------------------------------------------------------------------------
  // MODALS & ACTIONS
  // --------------------------------------------------------------------------
  function openInstallMachineModal(plotId) {
    activePlotIdForModal = plotId;
    const modal = document.getElementById('modalInstallMachine');
    const list = document.getElementById('modalMachineList');
    document.getElementById('modalPlotTitle').textContent = `Instalar Equipamento no Lote ${plotId + 1}`;

    list.innerHTML = '';
    const available = [...gameState.machines, ...(gameState.inventedItems || [])];

    available.forEach(m => {
      const row = document.createElement('div');
      row.className = 'machine-option-row';
      row.innerHTML = `
        <div style="display: flex; align-items: center; gap: 10px;">
          <span style="font-size: 1.8rem;">${m.icon}</span>
          <div>
            <strong>${m.name}</strong>
            <div style="font-size: 0.75rem; color: var(--text-muted);">
              Gera: <strong>+${m.outputRate} ${getCommodityName(m.outputId)}</strong> | Demanda: <strong>${m.power} MW</strong>
            </div>
          </div>
        </div>
        <div style="text-align: right;">
          <div style="font-family: var(--font-mono); font-weight: 700; color: var(--accent-amber);">${fmtCurrency(m.cost)}</div>
          <button class="btn btn-sm btn-primary" style="margin-top: 4px;">Instalar</button>
        </div>
      `;
      row.onclick = () => installMachine(plotId, m.id);
      list.appendChild(row);
    });

    modal.classList.add('active');
  }

  async function installMachine(plotId, machineId) {
    try {
      const resp = await fetch('/api/factory/place', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ plotId, machineId })
      });
      const data = await resp.json();
      if (!resp.ok) {
        showToast(data.error || 'Erro ao instalar máquina.', 'error');
      } else {
        playSound('upgrade');
        showToast(data.message, 'success');
        gameState.player = data.player;
        document.getElementById('modalInstallMachine').classList.remove('active');
        renderFactoryPlots();
        updateHeaderStats();
      }
    } catch (e) {
      showToast('Erro de rede ao instalar equipamento.', 'error');
    }
  }

  window.upgradePlot = async function (plotId) {
    try {
      const resp = await fetch('/api/factory/upgrade', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ plotId })
      });
      const data = await resp.json();
      if (!resp.ok) {
        showToast(data.error || 'Erro no upgrade.', 'error');
      } else {
        playSound('upgrade');
        showToast(data.message, 'success');
        gameState.player = data.player;
        renderFactoryPlots();
        updateHeaderStats();
      }
    } catch (e) {
      showToast('Falha na comunicação de upgrade.', 'error');
    }
  };

  async function advanceEra() {
    try {
      const resp = await fetch('/api/player/advance-era', {
        method: 'POST'
      });
      const data = await resp.json();
      if (!resp.ok) {
        showToast(data.error || 'Requisitos da Era não atendidos.', 'error');
      } else {
        playSound('coin');
        showToast(data.message, 'success');
        gameState.player = data.player;
        renderAll();
      }
    } catch (e) {
      showToast('Erro ao avançar Era.', 'error');
    }
  }

  // Contract Modal Handlers
  function openNewContractModal() {
    document.getElementById('modalNewContract').classList.add('active');
  }

  async function submitNewContract() {
    const to = document.getElementById('contractToInput').value.trim();
    const item = document.getElementById('contractItemSelect').value;
    const amount = document.getElementById('contractAmountInput').value;
    const pricePerUnit = document.getElementById('contractPriceInput').value;

    try {
      const resp = await fetch('/api/contract/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ to, item, amount, pricePerUnit })
      });
      const data = await resp.json();
      if (!resp.ok) {
        showToast(data.error || 'Erro ao publicar contrato.', 'error');
      } else {
        showToast(`Contrato ${data.contract.id} publicado no balcão P2P!`, 'success');
        document.getElementById('modalNewContract').classList.remove('active');
        playSound('click');
      }
    } catch (e) {
      showToast('Erro ao criar contrato.', 'error');
    }
  }

  // --------------------------------------------------------------------------
  // INITIALIZATION & EVENT LISTENERS
  // --------------------------------------------------------------------------
  function setupEventListeners() {
    // Nav Tabs
    document.querySelectorAll('.nav-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));

        btn.classList.add('active');
        const tabId = btn.getAttribute('data-tab');
        document.getElementById(tabId)?.classList.add('active');

        if (tabId === 'tab-market') {
          setTimeout(renderMarketChart, 50);
        }
        playSound('click');
      });
    });

    // Sound toggle
    document.getElementById('soundToggle').addEventListener('click', () => {
      soundEnabled = !soundEnabled;
      document.getElementById('soundToggle').textContent = soundEnabled ? '🔊' : '🔇';
      showToast(soundEnabled ? 'Efeitos sonoros ativados.' : 'Efeitos sonoros desativados.');
    });

    // Advance Era Button
    document.getElementById('btnAdvanceEra').addEventListener('click', advanceEra);

    // AI Synthesize Button
    document.getElementById('btnSynthesizeAI').addEventListener('click', synthesizeAI);

    // Lab select change
    ['selectInput1', 'selectInput2', 'selectCatalyst'].forEach(id => {
      document.getElementById(id)?.addEventListener('change', updateLabPreviews);
    });

    // Market selector
    document.getElementById('marketItemSelect').addEventListener('change', (e) => {
      currentSelectedCommodityId = e.target.value;
      renderMarketChart();
      renderOrderBook();
      playSound('click');
    });

    // Order side toggle
    const buyBtn = document.getElementById('btnSideBuy');
    const sellBtn = document.getElementById('btnSideSell');
    const submitBtn = document.getElementById('btnSubmitOrder');

    buyBtn.addEventListener('click', () => {
      orderSide = 'BUY';
      buyBtn.classList.add('active');
      sellBtn.classList.remove('active');
      submitBtn.className = 'btn btn-buy btn-block';
      submitBtn.textContent = 'Comprar no Livro P2P';
    });

    sellBtn.addEventListener('click', () => {
      orderSide = 'SELL';
      sellBtn.classList.add('active');
      buyBtn.classList.remove('active');
      submitBtn.className = 'btn btn-sell btn-block';
      submitBtn.textContent = 'Vender no Livro P2P';
    });

    // Order input changes
    document.getElementById('orderInputPrice').addEventListener('input', updateOrderTotal);
    document.getElementById('orderInputQty').addEventListener('input', updateOrderTotal);
    submitBtn.addEventListener('click', submitOrder);

    // Chat
    document.getElementById('btnSendChat').addEventListener('click', sendChatMessage);
    document.getElementById('chatInputText').addEventListener('keydown', (e) => {
      if (e.key === 'Enter') sendChatMessage();
    });

    // Modal close buttons
    document.getElementById('btnCloseInstallModal').addEventListener('click', () => {
      document.getElementById('modalInstallMachine').classList.remove('active');
    });
    document.getElementById('btnCloseContractModal').addEventListener('click', () => {
      document.getElementById('modalNewContract').classList.remove('active');
    });
    document.getElementById('btnOpenNewContractModal').addEventListener('click', openNewContractModal);
    document.getElementById('btnSubmitNewContract').addEventListener('click', submitNewContract);
  }

  // Initial Boot
  window.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
    initWebSocket();

    // Fallback initial state load via REST if WS is connecting
    fetch('/api/state')
      .then(r => r.json())
      .then(data => {
        if (!gameState) {
          gameState = data;
          renderAll();
        }
      })
      .catch(e => console.warn('Aguardando WebSocket...'));
  });

})();
