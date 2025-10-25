let stompClientMesas = null;
let isStompConnected = false;

function connectTableWebSocket() {
    const socket = new SockJS('/ws');
    stompClientMesas = Stomp.over(socket);
    stompClientMesas.connect({}, function () {
        isStompConnected = true;
        stompClientMesas.subscribe('/topic/table-status', function (message) {
            const tableDTO = JSON.parse(message.body);
            updateTableInView(tableDTO);
        });
        stompClientMesas.subscribe('/user/queue/errors', function (message) {
            const payload = message.body;
            if (payload) {
                showToast('Error: ' + payload);
            }
        });
        stompClientMesas.subscribe('/topic/table-errors', function (message) {
            const payload = message.body;
            if (payload) {
                showToast('Error: ' + payload);
            }
        });
    }, function (error) {
        console.warn('Error connecting STOMP:', error);
        isStompConnected = false;
    });
}

function updateTableInView(tableDTO) {
    const mesaElement = document.querySelector(`.mesa[data-numero="${tableDTO.number}"]`);
    if (mesaElement) {
        actualizarVistaMesa(mesaElement, tableDTO.status);
        actualizarResumen();
    }
}

function initMesaClickEvents() {
    document.querySelectorAll('.mesa').forEach(el => {
        el.addEventListener('click', function () {
            openTableStatusModal(el);
        });
    });
}

function sendTableUpdate(tableNumber, newStatus) {
    if (typeof stompClientMesas !== 'undefined' && isStompConnected) {
        try {
            const payloadObj = {tableNumber: parseInt(tableNumber, 10), status: newStatus};
            const payload = JSON.stringify(payloadObj);
            stompClientMesas.send('/app/mesas/update-status', {}, payload);
            const el = document.querySelector(`.mesa[data-numero="${tableNumber}"]`);
            if (el) {
                actualizarVistaMesa(el, newStatus);
                actualizarResumen();
            }
            closeTableStatusModal();
        } catch (e) {
            console.error('Error sending via STOMP:', e);
            showToast('No se pudo enviar la actualización via WebSocket. Por favor recarga la página e intenta de nuevo.');
        }
    } else {
        showToast('Conexión en tiempo real no disponible. Por favor recarga la página para reintentar.');
    }
}

function actualizarResumen() {
    const mesasEls = Array.from(document.querySelectorAll('.mesa'));
    const countV = mesasEls.filter(el => el.classList.contains('mesa-verde')).length;
    const countA = mesasEls.filter(el => el.classList.contains('mesa-azul')).length;
    const countAm = mesasEls.filter(el => el.classList.contains('mesa-amarillo')).length;
    const countR = mesasEls.filter(el => el.classList.contains('mesa-rojo')).length;

    const elVerde = document.getElementById('countVerde');
    const elAzul = document.getElementById('countAzul');
    const elAmar = document.getElementById('countAmarillo');
    const elRojo = document.getElementById('countRojo');

    if (elVerde) elVerde.textContent = countV;
    if (elAzul) elAzul.textContent = countA;
    if (elAmar) elAmar.textContent = countAm;
    if (elRojo) elRojo.textContent = countR;
}

function initMesasFromDOM() {
    document.querySelectorAll('.mesa').forEach(el => {
        if (!el.dataset.estado) el.dataset.estado = 'gris';

        const statusEnum = el.getAttribute('data-status-enum');

        if (statusEnum) {
            actualizarVistaMesa(el, statusEnum);
        } else {
            actualizarVistaMesa(el, 'FUERA_DE_SERVICIO');
        }
    });
    actualizarResumen();
}

function actualizarVistaMesa(mesaElement, statusEnum) {
    mesaElement.classList.remove('mesa-gris', 'mesa-verde', 'mesa-azul', 'mesa-amarillo', 'mesa-rojo');

    let claseColor = 'mesa-gris';
    let textoEstado = 'Sin estado';

    switch (statusEnum) {
        case 'DISPONIBLE':
            claseColor = 'mesa-verde';
            textoEstado = 'Mesa Libre';
            break;
        case 'ESPERANDO_PEDIDO':
            claseColor = 'mesa-azul';
            textoEstado = 'Esperando Pedido';
            break;
        case 'FALTA_ATENCION':
            claseColor = 'mesa-amarillo';
            textoEstado = 'Falta Atención';
            break;
        case 'PEDIDO_ENTREGADO':
            claseColor = 'mesa-rojo';
            textoEstado = 'Pedido Entregado';
            break;
        case 'FUERA_DE_SERVICIO':
        case 'OCUPADO':
        case 'RESERVADO':
            claseColor = 'mesa-gris';
            textoEstado = 'Sin estado';
            break;
    }

    mesaElement.classList.add(claseColor);
    const estadoEl = mesaElement.querySelector('.mesa-estado');
    if (estadoEl) estadoEl.textContent = textoEstado;
    mesaElement.setAttribute('data-status-enum', statusEnum);
}

function openTableStatusModal(mesaElement) {
    const overlay = document.getElementById('tableStatusModal');
    if (!overlay) return;

    const tableNumber = mesaElement.getAttribute('data-numero');
    const titleNumberSpan = overlay.querySelector('.modal-title-table-number');
    const hiddenInput = document.getElementById('modalTableNumberInput');

    if (titleNumberSpan) titleNumberSpan.textContent = tableNumber;
    if (hiddenInput) hiddenInput.value = tableNumber;

    const currentStatus = mesaElement.getAttribute('data-status-enum') || 'FUERA_DE_SERVICIO';
    overlay.querySelectorAll('.status-button').forEach(btn => {
        if (btn.dataset.status === currentStatus) {
            btn.classList.add('selected-status');
        } else {
            btn.classList.remove('selected-status');
        }
    });

    overlay.style.display = 'flex';
}

function closeTableStatusModal() {
    const overlay = document.getElementById('tableStatusModal');
    if (!overlay) return;
    overlay.style.display = 'none';

    overlay.querySelectorAll('.status-button').forEach(btn => btn.classList.remove('selected-status'));
}

function initModalEvents() {
    const overlay = document.getElementById('tableStatusModal');
    if (!overlay) return;

    const closeBtn = document.getElementById('closeTableStatusModal');
    const cancelarBtn = document.getElementById('cancelarTableStatus');

    if (closeBtn) closeBtn.addEventListener('click', closeTableStatusModal);
    if (cancelarBtn) cancelarBtn.addEventListener('click', closeTableStatusModal);

    overlay.addEventListener('click', function (event) {
        if (event.target === overlay) closeTableStatusModal();
    });

    overlay.querySelectorAll('.status-button').forEach(btn => {
        btn.addEventListener('click', function () {
            const status = btn.dataset.status;
            const tableNumber = document.getElementById('modalTableNumberInput').value;
            if (!tableNumber || !status) return;
            sendTableUpdate(tableNumber, status);
        });
    });
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () {
        initMesasFromDOM();
        initMesaClickEvents();
        initModalEvents();
    });
} else {
    initMesasFromDOM();
    initMesaClickEvents();
    initModalEvents();
}

function showToast(message, type = 'error', duration = 5000) {
    const container = document.getElementById('toastContainer');
    if (!container) {
        alert(message);
        return;
    }
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    container.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('fade-out');
        setTimeout(() => toast.remove(), 500);
    }, duration);
}
