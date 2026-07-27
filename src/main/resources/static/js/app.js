const API = '/api';
let currentTripId = null;
let selectedCardId = null;
let selectedCategory = 'Dining';
let cards = [];

document.addEventListener('DOMContentLoaded', () => {
    updateClock();
    setInterval(updateClock, 60000);
    loadCards();
    loadTrips();
    setupCategoryChips();
    setDefaultDates();
});

function updateClock() {
    const now = new Date();
    document.getElementById('clock').textContent =
        now.getHours().toString().padStart(2, '0') + ':' +
        now.getMinutes().toString().padStart(2, '0');
}

function setDefaultDates() {
    const today = new Date();
    const nextWeek = new Date(today);
    nextWeek.setDate(today.getDate() + 7);
    const twoWeeks = new Date(today);
    twoWeeks.setDate(today.getDate() + 14);
    document.getElementById('startDate').value = formatDate(nextWeek);
    document.getElementById('endDate').value = formatDate(twoWeeks);
}

function formatDate(d) {
    return d.toISOString().split('T')[0];
}

function showToast(msg) {
    const t = document.getElementById('toast');
    t.textContent = msg;
    t.classList.add('show');
    setTimeout(() => t.classList.remove('show'), 2500);
}

const titles = {
    home: ['Travel Lion', '全球安全通行证'],
    setup: ['创建行程', 'Phase 1: 出行前准备'],
    readiness: ['准备度检查', '您的旅行准备评分'],
    dashboard: ['旅行面板', 'Phase 2: 支付追踪'],
    alerts: ['安全警报', 'Phase 3: 可疑交易监控'],
    cards: ['卡片管理', '卡片控制与设置']
};

function showView(name) {
    document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
    document.getElementById('view-' + name).classList.add('active');
    document.querySelectorAll('.nav-item').forEach(n => {
        n.classList.toggle('active', n.dataset.view === name);
    });
    const [title, sub] = titles[name] || titles.home;
    document.getElementById('page-title').textContent = title;
    document.getElementById('page-subtitle').textContent = sub;

    if (name === 'dashboard' && currentTripId) loadDashboard();
    if (name === 'alerts' && currentTripId) loadAlerts();
    if (name === 'cards') loadCardList();
    if (name === 'home') loadTrips();
}

async function api(url, options = {}) {
    const res = await fetch(API + url, {
        headers: { 'Content-Type': 'application/json' },
        ...options
    });
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.error || Object.values(err).join(', ') || 'Request failed');
    }
    return res.json();
}

async function loadCards() {
    try {
        cards = await api('/cards');
        renderCardSelect();
        renderPaymentCardSelect();
    } catch (e) {
        showToast('加载卡片失败: ' + e.message);
    }
}

function renderCardSelect() {
    const container = document.getElementById('card-select');
    if (!cards.length) {
        container.innerHTML = '<p class="empty-state">暂无可用卡片</p>';
        return;
    }
    container.innerHTML = cards.map(c => `
        <div class="card-option ${selectedCardId === c.id ? 'selected' : ''}"
             onclick="selectCard(${c.id})">
            <div class="card-name">${c.cardName} •••• ${c.lastFourDigits}</div>
            <div class="card-info">
                余额: ${c.balance} | 限额: ${c.dailyLimit}/日
                ${c.overseasEnabled ? ' | ✅ 海外支付' : ' | ❌ 海外未开通'}
                ${c.status !== 'ACTIVE' ? ' | ⚠️ ' + c.status : ''}
            </div>
        </div>
    `).join('');
    if (!selectedCardId && cards.length) selectCard(cards[0].id);
}

function selectCard(id) {
    selectedCardId = id;
    renderCardSelect();
}

function renderPaymentCardSelect() {
    const sel = document.getElementById('payment-card');
    sel.innerHTML = cards.map(c =>
        `<option value="${c.id}">${c.cardName} •••• ${c.lastFourDigits}</option>`
    ).join('');
}

function setupCategoryChips() {
    document.querySelectorAll('#category-chips .chip').forEach(chip => {
        chip.addEventListener('click', () => {
            document.querySelectorAll('#category-chips .chip').forEach(c => c.classList.remove('selected'));
            chip.classList.add('selected');
            selectedCategory = chip.dataset.cat;
        });
    });
}

async function loadTrips() {
    try {
        const trips = await api('/trips');
        const container = document.getElementById('trip-list');
        if (!trips.length) {
            container.innerHTML = '<div class="empty-state"><div class="icon">✈️</div><p>还没有行程，创建一个开始吧！</p></div>';
            return;
        }
        container.innerHTML = trips.map(t => {
            const score = t.readinessScore || 0;
            const scoreClass = score >= 70 ? 'score-high' : score >= 40 ? 'score-medium' : 'score-low';
            return `
                <div class="trip-item" onclick="selectTrip(${t.id})">
                    <span class="trip-score ${scoreClass}">${score}分</span>
                    <div class="trip-dest">${t.destination}</div>
                    <div class="trip-dates">${t.startDate} → ${t.endDate} | ${t.currency} ${t.budget}</div>
                </div>
            `;
        }).join('');
    } catch (e) {
        showToast('加载行程失败');
    }
}

function selectTrip(id) {
    currentTripId = id;
    showView('dashboard');
}

async function createTrip(e) {
    e.preventDefault();
    if (!selectedCardId) { showToast('请选择一张卡片'); return; }
    try {
        const data = {
            destination: document.getElementById('destination').value,
            startDate: document.getElementById('startDate').value,
            endDate: document.getElementById('endDate').value,
            budget: parseFloat(document.getElementById('budget').value),
            currency: document.getElementById('currency').value,
            preferredCardId: selectedCardId
        };
        const result = await api('/trips', { method: 'POST', body: JSON.stringify(data) });
        currentTripId = result.trip.id;
        renderReadiness(result.readiness, result.trip);
        showView('readiness');
        showToast('行程创建成功！');
    } catch (e) {
        showToast('创建失败: ' + e.message);
    }
}

function renderReadiness(readiness, trip) {
    const score = readiness.score;
    const scoreClass = score >= 70 ? 'score-high' : score >= 40 ? 'score-medium' : 'score-low';
    const scoreLabel = score >= 70 ? '准备充分' : score >= 40 ? '需要改进' : '尚未就绪';

    let html = `
        <div class="score-display">
            <div class="score-circle ${scoreClass}">
                <span class="score-num">${score}</span>
                <span class="score-label">${scoreLabel}</span>
            </div>
            <p style="font-size:14px;color:#666">${trip.destination} | ${trip.startDate} → ${trip.endDate}</p>
        </div>
    `;

    if (readiness.warnings.length) {
        html += '<h3 style="margin:16px 0 8px">⚠️ 警告</h3>';
        readiness.warnings.forEach(w => {
            const cls = w.severity === 'ERROR' ? 'warning-error' : 'warning-warn';
            html += `<div class="warning-item ${cls}">${w.message}</div>`;
        });
    }

    if (readiness.recommendedActions.length) {
        html += '<h3 style="margin:16px 0 8px">💡 建议操作</h3>';
        readiness.recommendedActions.forEach(a => {
            html += `<div class="action-item">${a}</div>`;
        });
    }

    document.getElementById('readiness-card').innerHTML = html;
}

function goToDashboard() {
    showView('dashboard');
}

async function loadDashboard() {
    if (!currentTripId) {
        document.getElementById('dashboard-stats').innerHTML =
            '<div class="empty-state"><div class="icon">✈️</div><p>请先创建或选择一个行程</p></div>';
        return;
    }
    try {
        const [dashboard, transactions] = await Promise.all([
            api('/trips/' + currentTripId + '/dashboard'),
            api('/trips/' + currentTripId + '/transactions')
        ]);
        renderDashboard(dashboard);
        renderTransactions(transactions);
    } catch (e) {
        showToast('加载面板失败');
    }
}

function renderDashboard(d) {
    const pct = d.budget > 0 ? Math.min(100, (d.totalSpent / d.budget) * 100) : 0;
    document.getElementById('dashboard-stats').innerHTML = `
        <div class="stats-grid">
            <div class="stat-box">
                <div class="stat-value">${d.currency} ${d.budget}</div>
                <div class="stat-label">总预算</div>
            </div>
            <div class="stat-box">
                <div class="stat-value">${d.currency} ${d.totalSpent}</div>
                <div class="stat-label">已花费</div>
            </div>
            <div class="stat-box">
                <div class="stat-value">${d.currency} ${d.remainingBudget}</div>
                <div class="stat-label">剩余预算</div>
            </div>
            <div class="stat-box">
                <div class="stat-value">${d.alertCount}</div>
                <div class="stat-label">安全警报</div>
            </div>
        </div>
        <div class="card">
            <h3>${d.destination} - 预算使用</h3>
            <div class="budget-bar"><div class="budget-bar-fill" style="width:${pct}%"></div></div>
            <p style="font-size:12px;color:#666;text-align:right">${pct.toFixed(1)}% 已使用</p>
        </div>
    `;
}

function renderTransactions(txs) {
    const container = document.getElementById('transaction-list');
    if (!txs.length) {
        container.innerHTML = '<div class="empty-state"><p>暂无交易记录</p></div>';
        return;
    }
    container.innerHTML = txs.map(tx => {
        const isSuccess = tx.status === 'SUCCESS';
        let html = `
            <div class="tx-item">
                <div>
                    <div class="tx-merchant">${tx.merchant}</div>
                    <div class="tx-detail">${tx.category} | ${tx.currency} | 汇率: ${tx.exchangeRate}</div>
                    <div class="tx-detail">${new Date(tx.transactionTime).toLocaleString()}</div>
                </div>
                <div class="tx-amount ${isSuccess ? 'success' : 'failed'}">
                    ${isSuccess ? '-' : ''}${tx.amount}
                    <div style="font-size:11px;font-weight:400">${isSuccess ? '成功' : '失败'}</div>
                </div>
            </div>
        `;
        if (!isSuccess && tx.failureMessage) {
            html += `<div class="tx-failure">
                <strong>${tx.failureMessage}</strong>
                <div class="action">💡 ${tx.recommendedAction}</div>
            </div>`;
        }
        return html;
    }).join('');
}

async function processPayment(e) {
    e.preventDefault();
    if (!currentTripId) { showToast('请先选择行程'); return; }
    try {
        const data = {
            cardId: parseInt(document.getElementById('payment-card').value),
            merchant: document.getElementById('merchant').value,
            merchantLocation: document.getElementById('merchantLocation').value,
            amount: parseFloat(document.getElementById('amount').value),
            currency: document.getElementById('currency')?.value || 'USD',
            exchangeRate: parseFloat(document.getElementById('exchangeRate').value) || 1.0,
            category: selectedCategory
        };
        const trip = await api('/trips/' + currentTripId);
        data.currency = trip.currency;

        const result = await api('/trips/' + currentTripId + '/transactions', {
            method: 'POST', body: JSON.stringify(data)
        });

        if (result.status === 'SUCCESS') {
            showToast('支付成功！');
        } else {
            showToast('支付失败: ' + result.failureMessage);
        }
        document.getElementById('payment-form').reset();
        document.getElementById('exchangeRate').value = '1.0';
        loadDashboard();
        loadCards();
    } catch (e) {
        showToast('支付处理失败: ' + e.message);
    }
}

async function loadAlerts() {
    if (!currentTripId) {
        document.getElementById('alert-list').innerHTML =
            '<div class="empty-state"><div class="icon">🔔</div><p>请先选择行程</p></div>';
        return;
    }
    try {
        const alerts = await api('/trips/' + currentTripId + '/alerts');
        const container = document.getElementById('alert-list');
        if (!alerts.length) {
            container.innerHTML = '<div class="empty-state"><div class="icon">✅</div><p>暂无安全警报，一切正常！</p></div>';
            return;
        }
        container.innerHTML = alerts.map(a => `
            <div class="alert-item risk-${a.riskLevel.toLowerCase()}">
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span class="risk-badge ${a.riskLevel}">${a.riskLevel} 风险</span>
                    <span class="status-badge ${a.status}">${statusLabel(a.status)}</span>
                </div>
                <div class="alert-reason">${a.reason}</div>
                <div class="tx-detail">交易: ${a.transaction.merchant} - ${a.transaction.amount} ${a.transaction.currency}</div>
                <div class="tx-detail">${new Date(a.createdAt).toLocaleString()}</div>
                ${a.status === 'PENDING' ? `
                    <div class="alert-actions">
                        <button class="btn btn-sm btn-success" onclick="alertAction(${a.id},'confirm')">确认正常</button>
                        <button class="btn btn-sm btn-warning" onclick="alertAction(${a.id},'report')">报告欺诈</button>
                        <button class="btn btn-sm btn-danger" onclick="alertAction(${a.id},'freeze')">冻结卡片</button>
                    </div>
                ` : ''}
            </div>
        `).join('');
    } catch (e) {
        showToast('加载警报失败');
    }
}

function statusLabel(s) {
    const map = { PENDING: '待处理', CONFIRMED: '已确认', REPORTED: '已报告', FROZEN: '已冻结' };
    return map[s] || s;
}

async function alertAction(alertId, action) {
    try {
        await api('/alerts/' + alertId + '/' + action, { method: 'POST' });
        const msgs = { confirm: '已确认交易正常', report: '欺诈报告已提交', freeze: '卡片已冻结' };
        showToast(msgs[action]);
        loadAlerts();
        loadCards();
    } catch (e) {
        showToast('操作失败: ' + e.message);
    }
}

async function loadCardList() {
    try {
        cards = await api('/cards');
        const container = document.getElementById('card-list');
        container.innerHTML = cards.map(c => `
            <div class="card">
                <h3>${c.cardName} •••• ${c.lastFourDigits}</h3>
                <div class="tx-detail">到期: ${c.expiryDate} | 状态: ${c.status}</div>
                <div class="tx-detail">余额: ${c.balance} | 日限额: ${c.dailyLimit}</div>
                <div class="tx-detail">${c.overseasEnabled ? '✅ 海外支付已开通' : '❌ 海外支付未开通'}</div>
                <div class="alert-actions" style="margin-top:12px">
                    ${!c.overseasEnabled ? `<button class="btn btn-sm btn-primary" onclick="enableOverseas(${c.id})">开通海外支付</button>` : ''}
                    ${c.status === 'FROZEN' ? `<button class="btn btn-sm btn-success" onclick="unfreezeCard(${c.id})">解冻卡片</button>` : ''}
                    ${c.status === 'ACTIVE' ? `<button class="btn btn-sm btn-danger" onclick="freezeCard(${c.id})">冻结卡片</button>` : ''}
                    <button class="btn btn-sm btn-secondary" onclick="increaseLimit(${c.id})">提高限额</button>
                </div>
            </div>
        `).join('');
    } catch (e) {
        showToast('加载卡片失败');
    }
}

async function enableOverseas(cardId) {
    await api('/cards/' + cardId + '/controls', {
        method: 'PUT', body: JSON.stringify({ enableOverseas: true })
    });
    showToast('海外支付已开通');
    loadCardList();
    loadCards();
}

async function increaseLimit(cardId) {
    const newLimit = prompt('请输入新的日限额:', '10000');
    if (!newLimit) return;
    await api('/cards/' + cardId + '/controls', {
        method: 'PUT', body: JSON.stringify({ newDailyLimit: parseFloat(newLimit) })
    });
    showToast('日限额已更新');
    loadCardList();
    loadCards();
}

async function freezeCard(cardId) {
    await api('/cards/' + cardId + '/freeze', { method: 'POST' });
    showToast('卡片已冻结');
    loadCardList();
    loadCards();
}

async function unfreezeCard(cardId) {
    await api('/cards/' + cardId + '/unfreeze', { method: 'POST' });
    showToast('卡片已解冻');
    loadCardList();
    loadCards();
}
