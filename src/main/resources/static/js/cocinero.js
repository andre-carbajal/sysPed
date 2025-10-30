let stompClientCocinero = null;
let isCocineroStompConnected = false;
let cocineroInitialized = false;

function connectOrderWebSocketCocinero() {
    if (stompClientCocinero !== null && isCocineroStompConnected) {
        try { stompClientCocinero.disconnect(); } catch (e) { console.warn(e); }
    }
    const socket = new SockJS('/ws');
    stompClientCocinero = Stomp.over(socket);
    stompClientCocinero.connect({}, function() {
        isCocineroStompConnected = true;
        stompClientCocinero.subscribe('/topic/order-updates', function(message) {
            const dto = JSON.parse(message.body);
            refreshOrders(true);
        });
        stompClientCocinero.subscribe('/user/queue/errors', function(message) {
            const payload = message.body; if (payload) showToast('Error: ' + payload);
        });
    }, function(err){ console.warn('STOMP Err', err); isCocineroStompConnected = false; });
}

function disconnectOrderWebSocketCocinero() {
    if (stompClientCocinero !== null && isCocineroStompConnected) {
        try { stompClientCocinero.disconnect(); isCocineroStompConnected = false; stompClientCocinero = null; } catch(e){console.warn(e);}    }
}

function initCocineroTabEvents() {
    // No hay filtro en la UI; siempre mostramos PENDIENTE y EN_PREPARACION
    const closeModal = document.getElementById('closeChangeOrderModal');
    const cancelBtn = document.getElementById('cancelChangeOrder');
    if (closeModal) closeModal.addEventListener('click', closeChangeOrderModal);
    if (cancelBtn) cancelBtn.addEventListener('click', closeChangeOrderModal);

    refreshOrders(false);
    connectOrderWebSocketCocinero();
    cocineroInitialized = true;
}

function closeChangeOrderModal() {
    const overlay = document.getElementById('changeOrderStatusModal');
    if (overlay) overlay.style.display = 'none';
}

function openChangeOrderModal(order) {
    const overlay = document.getElementById('changeOrderStatusModal');
    if (!overlay) return;
    // Obtener estado más reciente del pedido
    fetch(`/dashboard/orders/${order.id}`)
        .then(resp => {
            if (!resp.ok) throw new Error('No se pudo obtener el pedido ' + order.id);
            return resp.json();
        })
        .then(latest => {
            document.getElementById('modalOrderId').value = latest.id;
            document.getElementById('modalOrderInfo').textContent = `Pedido #${latest.id} - Mesa ${latest.tableNumber} - Estado: ${latest.status}`;
            const container = document.getElementById('modalStatusButtons');
            container.innerHTML = '';
            const allowed = getAllowedStatusesForOrder(latest.status);
            if (!allowed || allowed.length === 0) {
                const p = document.createElement('p');
                p.textContent = 'No hay acciones disponibles para este estado.';
                container.appendChild(p);
            } else {
                allowed.forEach(s => {
                    const btn = document.createElement('button');
                    btn.className = 'btn btn-primary status-change-btn';
                    btn.textContent = s;
                    btn.dataset.target = s;
                    btn.addEventListener('click', () => changeOrderStatus(latest.id, s));
                    container.appendChild(btn);
                });
            }
            overlay.style.display = 'flex';
        })
        .catch(err => {
            alert('No se pudo cargar el pedido: ' + err.message);
        });
}

function getAllowedStatusesForOrder(current) {
    switch (current) {
        case 'PENDIENTE': return ['EN_PREPARACION','CANCELADO'];
        case 'EN_PREPARACION': return ['LISTO','CANCELADO'];
        case 'LISTO': return ['PAGADO'];
        default: return [];
    }
}

function changeOrderStatus(orderId, newStatus) {
    fetch(`/dashboard/orders/${orderId}/status`, {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify({status: newStatus})
    }).then(async resp => {
        const text = await resp.text();
        if (!resp.ok) {
            // Si el servidor devuelve mensaje, úsalo; si no, muestra código
            const message = text && text.trim() ? text : ('Error: ' + resp.status);
            throw new Error(message);
        }
        // Si la respuesta es 200/204 con body vacío, tratar como éxito
        if (!text || !text.trim()) {
            return {};
        }
        try {
            return JSON.parse(text);
        } catch (e) {
            // No es JSON pero fue OK: tratar como éxito
            return {};
        }
    }).then(updated => {
        closeChangeOrderModal();
        refreshOrders(true);
    }).catch(err => { alert('No se pudo actualizar: ' + err.message); });
}

function renderOrders(list) {
    const container = document.getElementById('ordersContainer');
    if (!container) return;
    if (!list || list.length === 0) {
        container.innerHTML = '<p>No hay pedidos.</p>';
        return;
    }
    container.innerHTML = '';
    list.forEach(o => {
        const card = document.createElement('div');
        card.className = 'order-card';
        // añadir clase por estado
        if (o.status === 'PENDIENTE') {
            card.classList.add('order-pendiente');
        } else if (o.status === 'EN_PREPARACION') {
            card.classList.add('order-en-preparacion');
        }
        let estadoBadge = '';
        if (o.status === 'PENDIENTE') {
            estadoBadge = '<span class="order-status-badge badge-pendiente">Pendiente</span>';
        } else if (o.status === 'EN_PREPARACION') {
            estadoBadge = '<span class="order-status-badge badge-en-preparacion">En preparación</span>';
        } else {
            estadoBadge = `<span class="order-status-badge">${o.status}</span>`;
        }
        card.innerHTML = `<div style="display:flex; justify-content:space-between; align-items:center;"><div><strong>#${o.id}</strong> - Mesa ${o.tableNumber} - <em>${o.dateAndTimeOrder}</em></div><div>${estadoBadge}</div></div>
                          <div style="margin-top:8px;">Items:</div>`;
        const ul = document.createElement('ul');
        (o.items || []).forEach(i => {
            const li = document.createElement('li');
            li.textContent = `${i.quantity} x ${i.plateName} (${i.notes || ''})`;
            ul.appendChild(li);
        });
        card.appendChild(ul);
        const actions = document.createElement('div');
        actions.style.marginTop = '8px';
        const btn = document.createElement('button');
        btn.className = 'btn btn-secondary';
        btn.textContent = 'Cambiar estado';
        btn.addEventListener('click', () => openChangeOrderModal(o));
        actions.appendChild(btn);
        card.appendChild(actions);
        container.appendChild(card);
    });
}

function refreshOrders(force) {
    // Siempre solicitamos solo PENDIENTE y EN_PREPARACION
    const url = `/dashboard/orders?status=${encodeURIComponent('PENDIENTE,EN_PREPARACION')}`;
    fetch(url).then(resp => {
        if (!resp.ok) throw new Error('No se pudo obtener pedidos');
        return resp.json();
    }).then(list => renderOrders(list)).catch(err => {
        document.getElementById('ordersContainer').innerHTML = '<p>Error al cargar pedidos.</p>';
    });
}

function cleanupCocinero() {
    disconnectOrderWebSocketCocinero();
    cocineroInitialized = false;
}

function initializeCocinero() {
    if (!cocineroInitialized) initCocineroTabEvents();
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        if (!cocineroInitialized) initializeCocinero();
    });
} else {
    if (!cocineroInitialized) initializeCocinero();
}
