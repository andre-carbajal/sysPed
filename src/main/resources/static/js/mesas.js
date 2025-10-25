let stompClientMesas = null;

function connectTableWebSocket() {
    const socket = new SockJS('/ws');
    stompClientMesas = Stomp.over(socket);
    stompClientMesas.connect({}, function () {
        stompClientMesas.subscribe('/topic/table-status', function (message) {
            const tableDTO = JSON.parse(message.body);
            updateTableInView(tableDTO);
        });
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
        el.addEventListener('click', function() {
            const nuevoEstado = prompt("Nuevo estado (DISPONIBLE, OCUPADO, ESPERANDO_PEDIDO, FALTA_ATENCION, PEDIDO_ENTREGADO, FUERA_DE_SERVICIO):");

            if (nuevoEstado) {
                const tableNumber = el.getAttribute('data-numero');
                sendTableUpdate(tableNumber, nuevoEstado.toUpperCase());
            }
        });
    });
}

function sendTableUpdate(tableNumber, newStatus) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch('/dashboard/mesas/update-status', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            [header]: token
        },
        body: body
    })
        .then(response => response.text())
        .then(result => {
            if (result !== 'OK') {
                alert(result);
            }
        })
        .catch(() => {
            alert('Error de red al actualizar la mesa.');
        });

    function actualizarResumen() {
    const mesasEls = Array.from(document.querySelectorAll('.mesa'));
    const countV = mesasEls.filter(el => el.classList.contains('mesa-verde')).length;
    const countA = mesasEls.filter(el => el.classList.contains('mesa-azul')).length;
    const countAm = mesasEls.filter(el => el.classList.contains('mesa-amarillo')).length;
    const countR = mesasEls.filter(el => el.classList.contains('mesa-rojo')).length;

    document.getElementById('countVerde').textContent = countV;
    document.getElementById('countAzul').textContent = countA;
    document.getElementById('countAmarillo').textContent = countAm;
    document.getElementById('countRojo').textContent = countR;
}

function initMesasFromDOM() {
    document.querySelectorAll('.mesa').forEach(el => {
        if (!el.dataset.estado) el.dataset.estado = 'gris';

        const estado = el.dataset.estado;
        const statusEnum = el.getAttribute('data-status-enum');

        if (statusEnum) {
            actualizarVistaMesa(el, statusEnum);
        } else {
            actualizarVistaMesa(el, 'FUERA_DE_SERVICIO');
        }
    });
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
    mesaElement.querySelector('.mesa-estado').textContent = textoEstado;
    mesaElement.setAttribute('data-status-enum', statusEnum);
    }
}